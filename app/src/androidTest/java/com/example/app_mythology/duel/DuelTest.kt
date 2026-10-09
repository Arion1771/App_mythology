package com.example.app_mythology.duel

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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Mode Duel (Classique et QCM) — branche Test-Non-Regression : mise en place
 * d'une partie à 2 joueurs (nombre, noms, type, difficulté, pool de
 * questions, annonce de tour) puis première question : bonne réponse au 1er
 * et au 2e essai (Classique), deux essais faux, bonne et mauvaise réponse
 * (QCM), chacune menant au récapitulatif avec le statut et les points
 * attendus ; puis partie complète (tous les tours des deux joueurs) jusqu'au
 * classement final, avec vérification des scores et de leur affichage.
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

    /** Statut et score du premier joueur sur la première question. */
    private fun assertFirstResult(scenario: ActivityScenario<MainActivity>, vm: DuelViewModel, name: String, expected: String) {
        val player = scenario.onMain { vm.players.value!!.first() }
        assertEquals("Statut de la question « $name »", expected, player.results[0])
        if (expected == "red") assertEquals("Aucun point pour une question ratée", 0.0, player.score, 0.0)
        else assertTrue("Des points doivent être accordés", player.score > 0.0)
    }

    private fun classic(answers: (name: String) -> List<String>, expected: String) {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        val vm = startDuel(scenario, R.id.btn_primary_1, R.id.btn_duel_pool_same, R.id.duelQuestionClassicFragment)
        onView(withId(R.id.et_answer)).check(matches(isDisplayed()))
        val name = scenario.onMain { vm.currentQuestion()?.name }
        assertNotNull("Le duel doit avoir une question courante", name)

        // Le passage au 2e essai vide le champ : chaque essai est une nouvelle saisie.
        answers(name!!).forEachIndexed { i, answer ->
            answerAndValidate(answer, R.id.et_answer, R.id.btn_validate, scrollable = true)
            if (i == 0 && answer == WRONG_ANSWER) waitFor { scenario.onMain { vm.currentStep.value } == 2 }
        }
        scenario.waitForDestination(R.id.duelRecapFragment)
        onView(withId(R.id.tv_result_status)).check(matches(isDisplayed()))
        assertFirstResult(scenario, vm, name, expected)
        scenario.close()
    }

    private fun qcm(correct: Boolean) {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        // Le pool « questions différentes » est couvert ici, « mêmes questions » en Classique.
        val vm = startDuel(scenario, R.id.btn_primary_2, R.id.btn_duel_pool_different, R.id.duelQuestionQcmFragment)
        waitFor { scenario.onMain { vm.qcmChoices.value }.orEmpty().isNotEmpty() }
        val name = scenario.onMain { vm.currentQuestion()?.name }!!
        val choices = scenario.onMain { vm.qcmChoices.value }.orEmpty()
        val index = if (correct) choices.indexOf(name) else choices.indexOfFirst { it != name }
        assertTrue("Choix introuvable pour « $name » parmi $choices", index in QCM_CHOICE_BUTTONS.indices)

        onView(withId(QCM_CHOICE_BUTTONS[index])).perform(click())
        scenario.waitForDestination(R.id.duelRecapFragment)
        onView(withId(R.id.tv_result_status)).check(matches(isDisplayed()))
        assertFirstResult(scenario, vm, name, if (correct) "green" else "red")
        scenario.close()
    }

    @Test
    fun t01_classicGoodAnswerFirstTry() = classic({ listOf(it) }, expected = "green")

    @Test
    fun t02_classicGoodAnswerSecondTry() = classic({ listOf(WRONG_ANSWER, it) }, expected = "yellow")

    @Test
    fun t03_classicTwoWrongAnswersReachRecap() = classic({ listOf(WRONG_ANSWER, WRONG_ANSWER) }, expected = "red")

    @Test
    fun t04_qcmGoodAnswer() = qcm(correct = true)

    @Test
    fun t05_qcmWrongAnswer() = qcm(correct = false)

    /**
     * Joue toute la partie (tous les tours des deux joueurs) : à chaque tour,
     * [play] répond pour le joueur courant et renvoie les points attendus ;
     * vérifie ensuite les scores finaux et le classement affiché.
     */
    private fun fullDuel(
        modeButton: Int, poolButton: Int, questionDest: Int,
        play: (vm: DuelViewModel, scenario: ActivityScenario<MainActivity>, player: Int, round: Int) -> Double,
    ) {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        val vm = startDuel(scenario, modeButton, poolButton, questionDest)
        val expected = DoubleArray(2)
        while (true) {
            scenario.waitForDestination(questionDest)
            val (player, round) = scenario.onMain { (vm.currentPlayerIndex.value ?: 0) to (vm.currentRound.value ?: 0) }
            expected[player] += play(vm, scenario, player, round)
            scenario.waitForDestination(R.id.duelRecapFragment)
            val wasLast = scenario.onMain { vm.isLastTurn() }
            onView(withId(R.id.btn_result_next)).perform(scrollTo(), click()) // joueur suivant / classement
            if (wasLast) break
            scenario.waitForDestination(R.id.duelAnnounceFragment)
            onView(withId(R.id.btn_duel_announce_start)).perform(click())
        }

        scenario.waitForDestination(R.id.duelResultFragment)
        val players = scenario.onMain { vm.players.value!! }
        players.forEachIndexed { i, p -> assertEquals("Score final de ${p.name}", expected[i], p.score, 1e-9) }
        val expectedRows = players.sortedByDescending { it.score }
            .mapIndexed { i, p -> "${i + 1}. ${p.name} — ${formatScoreLikeApp(p.score)} pts" }
        val shownRows = scenario.onMain { activity ->
            val container = activity.findViewById<LinearLayout>(R.id.container_ranking)
            (0 until container.childCount).map { (container.getChildAt(it) as TextView).text.toString() }
        }
        assertEquals("Classement affiché", expectedRows, shownRows)
        scenario.close()
    }

    /** Bon au 1er essai / bon au 2e / raté, en rotation, décalée d'un cran pour le joueur 2. */
    @Test
    fun t06_classicFullDuelScoresAndRanking() = fullDuel(
        R.id.btn_primary_1, R.id.btn_duel_pool_same, R.id.duelQuestionClassicFragment
    ) { vm, scenario, player, round ->
        val question = scenario.onMain { vm.currentQuestion()!! }
        val (answers, fraction) = when ((round + player) % 3) {
            0 -> listOf(question.name) to 1.0
            1 -> listOf(WRONG_ANSWER, question.name) to 0.5
            else -> listOf(WRONG_ANSWER, WRONG_ANSWER) to 0.0
        }
        answers.forEachIndexed { attempt, answer ->
            answerAndValidate(answer, R.id.et_answer, R.id.btn_validate, scrollable = true)
            if (attempt == 0 && answers.size == 2) waitFor { scenario.onMain { vm.currentStep.value } == 2 }
        }
        question.difficulty * fraction
    }

    /** Joueur 1 : bonne réponse un tour sur deux ; joueur 2 : toujours la bonne réponse. */
    @Test
    fun t07_qcmFullDuelScoresAndRanking() = fullDuel(
        R.id.btn_primary_2, R.id.btn_duel_pool_different, R.id.duelQuestionQcmFragment
    ) { vm, scenario, player, round ->
        val question = scenario.onMain { vm.currentQuestion()!! }
        waitFor { scenario.onMain { vm.qcmChoices.value.orEmpty().contains(question.name) } }
        val choices = scenario.onMain { vm.qcmChoices.value!! }
        val correct = player == 1 || round % 2 == 0
        val index = if (correct) choices.indexOf(question.name) else choices.indexOfFirst { it != question.name }
        onView(withId(QCM_CHOICE_BUTTONS[index])).perform(click())
        if (correct) question.difficulty.toDouble() else 0.0
    }
}
