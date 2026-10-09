package com.example.app_mythology.duel

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.QCM_CHOICE_BUTTONS
import com.example.app_mythology.R
import com.example.app_mythology.WRONG_ANSWER
import com.example.app_mythology.answerAndValidate
import com.example.app_mythology.assertCurrentDestination
import com.example.app_mythology.graphViewModel
import com.example.app_mythology.onMain
import com.example.app_mythology.ui.MainActivity
import com.example.app_mythology.viewmodel.DuelViewModel
import com.example.app_mythology.waitFor
import com.example.app_mythology.waitForDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Mode Duel (Classique et QCM) — branche Test-Non-Regression : mise en place
 * d'une partie à 2 joueurs (nombre, noms, type, difficulté, pool de
 * questions, annonce de tour) puis première question : bonne réponse au 1er
 * et au 2e essai (Classique), deux essais faux, bonne et mauvaise réponse
 * (QCM), chacune menant au récapitulatif avec le statut et les points
 * attendus. Une partie complète nécessiterait de répondre à toutes les
 * questions de chaque joueur ; ces tests couvrent le premier tour.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DuelTest {

    /** Mise en place d'un duel à 2 joueurs (noms par défaut, niveau facile) jusqu'à la première question. */
    private fun startDuel(
        scenario: ActivityScenario<MainActivity>, modeButton: Int, poolButton: Int, questionDest: Int,
    ): DuelViewModel {
        onView(withId(R.id.btn_primary_3)).perform(click()) // Home -> Duel (nombre de joueurs)
        scenario.waitForDestination(R.id.duelPlayerCountFragment)
        onView(withId(R.id.btn_duel_players_next)).perform(click()) // 2 joueurs par défaut
        scenario.assertCurrentDestination(R.id.duelPlayerNamesFragment)
        onView(withId(R.id.btn_duel_names_next)).perform(click()) // noms par défaut (Joueur 1/2)
        scenario.assertCurrentDestination(R.id.duelModeChoiceFragment)

        onView(withId(modeButton)).perform(click())
        scenario.assertCurrentDestination(R.id.duelDifficultyChoiceFragment)
        onView(withId(R.id.btn_level_easy)).perform(click())
        scenario.assertCurrentDestination(R.id.duelPoolChoiceFragment)
        onView(withId(poolButton)).perform(click())
        scenario.waitForDestination(R.id.duelAnnounceFragment)
        onView(withId(R.id.tv_duel_announce_player)).check(matches(isDisplayed()))

        onView(withId(R.id.btn_duel_announce_start)).perform(click())
        scenario.waitForDestination(questionDest)
        return scenario.graphViewModel(R.id.duel_graph)
    }

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
}
