package com.example.app_mythology.n4_duel

import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.DeviceStateRestoreRule
import com.example.app_mythology.QCM_CHOICE_BUTTONS
import com.example.app_mythology.R
import com.example.app_mythology.WRONG_ANSWER
import com.example.app_mythology.answerAndValidate
import com.example.app_mythology.formatScoreLikeApp
import com.example.app_mythology.onMain
import com.example.app_mythology.startDuel
import com.example.app_mythology.ui.MainActivity
import com.example.app_mythology.viewmodel.DuelViewModel
import com.example.app_mythology.waitFor
import com.example.app_mythology.waitForDestination
import org.junit.Assert.assertEquals
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Mode Duel (Classique et QCM) — branche Test-Non-Regression : mise en place
 * d'une partie à 2 joueurs (nombre, noms, type, difficulté, pool de
 * questions, annonce de tour), puis partie complète, tous les tours des deux
 * joueurs, jusqu'au classement final. Chaque réponse mène au récapitulatif ;
 * sont vérifiés le statut de chaque question pour chaque joueur (green /
 * yellow / red), les scores finaux et le classement affiché.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DuelTest {

    /**
     * Précaution : le duel n'écrit aujourd'hui rien sur l'appareil (ni rencontres,
     * ni niveaux internes, ni succès), mais l'état est tout de même restauré.
     */
    @get:Rule
    val deviceState = DeviceStateRestoreRule()

    /** Points obtenus et statut attendu pour une question. */
    private data class Outcome(val points: Double, val status: String)

    /**
     * Joue toute la partie : à chaque tour, [play] répond pour le joueur courant
     * et renvoie le résultat attendu ; vérifie ensuite les statuts de chaque
     * question, les scores finaux et le classement affiché.
     */
    private fun fullDuel(
        modeButton: Int, poolButton: Int, questionDest: Int,
        play: (vm: DuelViewModel, scenario: ActivityScenario<MainActivity>, player: Int, round: Int) -> Outcome,
    ) {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        val vm = startDuel(scenario, modeButton, poolButton, questionDest)
        val expectedScores = DoubleArray(2)
        val expectedStatuses = List(2) { mutableListOf<String>() }
        while (true) {
            scenario.waitForDestination(questionDest)
            val (player, round) = scenario.onMain { (vm.currentPlayerIndex.value ?: 0) to (vm.currentRound.value ?: 0) }
            val outcome = play(vm, scenario, player, round)
            expectedScores[player] += outcome.points
            expectedStatuses[player] += outcome.status

            scenario.waitForDestination(R.id.duelRecapFragment)
            onView(withId(R.id.tv_result_status)).check(matches(isDisplayed()))
            val wasLast = scenario.onMain { vm.isLastTurn() }
            onView(withId(R.id.btn_result_next)).perform(scrollTo(), click()) // joueur suivant / classement
            if (wasLast) break
            scenario.waitForDestination(R.id.duelAnnounceFragment)
            onView(withId(R.id.btn_duel_announce_start)).perform(click())
        }

        scenario.waitForDestination(R.id.duelResultFragment)
        val players = scenario.onMain { vm.players.value!! }
        players.forEachIndexed { i, p ->
            assertEquals("Statuts des questions de ${p.name}", expectedStatuses[i], p.results)
            assertEquals("Score final de ${p.name}", expectedScores[i], p.score, 1e-9)
        }
        val expectedRows = players.sortedByDescending { it.score }
            .mapIndexed { i, p -> "${i + 1}. ${p.name} — ${formatScoreLikeApp(p.score)} pts" }
        val shownRows = scenario.onMain { activity ->
            val container = activity.findViewById<LinearLayout>(R.id.container_ranking)
            (0 until container.childCount).map { (container.getChildAt(it) as TextView).text.toString() }
        }
        assertEquals("Classement affiché", expectedRows, shownRows)
        scenario.close()
    }

    /**
     * Classique, mêmes questions pour les deux joueurs : bon au 1er essai (points
     * pleins) / bon au 2e essai (moitié) / deux essais faux (0), en rotation,
     * décalée d'un cran pour le joueur 2.
     */
    @Test
    fun t01_classicFullDuel() = fullDuel(
        R.id.btn_primary_1, R.id.btn_duel_pool_same, R.id.duelQuestionClassicFragment
    ) { vm, scenario, player, round ->
        onView(withId(R.id.et_answer)).check(matches(isDisplayed()))
        val question = scenario.onMain { vm.currentQuestion()!! }
        val (answers, outcome) = when ((round + player) % 3) {
            0 -> listOf(question.name) to Outcome(question.difficulty.toDouble(), "green")
            1 -> listOf(WRONG_ANSWER, question.name) to Outcome(question.difficulty / 2.0, "yellow")
            else -> listOf(WRONG_ANSWER, WRONG_ANSWER) to Outcome(0.0, "red")
        }
        // Le passage au 2e essai vide le champ : chaque essai est une nouvelle saisie.
        answers.forEachIndexed { attempt, answer ->
            answerAndValidate(answer, R.id.et_answer, R.id.btn_validate, scrollable = true)
            if (attempt == 0 && answers.size == 2) waitFor { scenario.onMain { vm.currentStep.value } == 2 }
        }
        outcome
    }

    /**
     * QCM, questions différentes pour chaque joueur : joueur 1 bon un tour sur
     * deux (points pleins / 0), joueur 2 toujours bon.
     */
    @Test
    fun t02_qcmFullDuel() = fullDuel(
        R.id.btn_primary_2, R.id.btn_duel_pool_different, R.id.duelQuestionQcmFragment
    ) { vm, scenario, player, round ->
        val question = scenario.onMain { vm.currentQuestion()!! }
        waitFor { scenario.onMain { vm.qcmChoices.value.orEmpty().contains(question.name) } }
        QCM_CHOICE_BUTTONS.forEach { onView(withId(it)).check(matches(isDisplayed())) }
        val choices = scenario.onMain { vm.qcmChoices.value!! }
        val correct = player == 1 || round % 2 == 0
        val index = if (correct) choices.indexOf(question.name) else choices.indexOfFirst { it != question.name }
        onView(withId(QCM_CHOICE_BUTTONS[index])).perform(click())
        if (correct) Outcome(question.difficulty.toDouble(), "green") else Outcome(0.0, "red")
    }
}
