package com.example.app_mythology

import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.ui.MainActivity
import com.example.app_mythology.viewmodel.DuelViewModel
import com.example.app_mythology.viewmodel.QuizViewModel
import org.hamcrest.Matcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

private const val WRONG = "__reponse_forcement_fausse__"

private val QCM_BUTTONS = listOf(
    R.id.btn_qcm_choice_0, R.id.btn_qcm_choice_1, R.id.btn_qcm_choice_2, R.id.btn_qcm_choice_3
)

private fun navHostOf(activity: MainActivity) =
    activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

/** Exécute [block] sur le thread principal et en renvoie le résultat. */
private fun <T> ActivityScenario<MainActivity>.onMain(block: (MainActivity) -> T): T {
    var result: T? = null
    onActivity { result = block(it) }
    @Suppress("UNCHECKED_CAST")
    return result as T
}

/** ViewModel partagé par un graphe de navigation (quiz Classique / QCM, Duel). */
private inline fun <reified VM : androidx.lifecycle.ViewModel> ActivityScenario<MainActivity>.graphVm(graphId: Int): VM =
    onMain { ViewModelProvider(navHostOf(it).navController.getBackStackEntry(graphId))[VM::class.java] }

/** ViewModel propre au fragment affiché (quiz Lieux / Liste). */
private fun ActivityScenario<MainActivity>.currentFragmentQuizVm(): QuizViewModel = onMain {
    val fragment: Fragment = navHostOf(it).childFragmentManager.primaryNavigationFragment!!
    ViewModelProvider(fragment)[QuizViewModel::class.java]
}

/** Saisie directe (accents compris, ce que typeText ne sait pas taper) puis clic sur Valider. */
private fun answerAndValidate(answer: String, inputId: Int, validateId: Int, scrollable: Boolean) {
    onView(withId(inputId)).perform(replaceText(answer))
    closeSoftKeyboard()
    if (scrollable) onView(withId(validateId)).perform(scrollTo(), click())
    else onView(withId(validateId)).perform(click())
}

/** Clique le premier bouton enfant d'un conteneur créé dynamiquement (thèmes du mode Liste). */
private fun clickFirstChildButton(): ViewAction = object : ViewAction {
    override fun getConstraints(): Matcher<android.view.View> = IsInstanceOf.instanceOf(ViewGroup::class.java)
    override fun getDescription() = "clique le premier bouton enfant"
    override fun perform(uiController: UiController, view: android.view.View) {
        val group = view as ViewGroup
        (0 until group.childCount).map { group.getChildAt(it) }.first { it is Button }.performClick()
        uiController.loopMainThreadUntilIdle()
    }
}

/**
 * Bonne réponse dans chaque mode de jeu — branche Test-Non-Regression.
 *
 * Les questions étant tirées au hasard, la bonne réponse est lue dans le
 * ViewModel de l'écran (question courante) puis saisie ou choisie comme le
 * ferait un joueur. Vérifie le statut enregistré pour la question (green =
 * trouvée au 1er essai, yellow = au 2e essai), les points accordés et le
 * passage à l'écran de résultat :
 * - Classique (entités, artéfacts) et Duel Classique : 1er et 2e essai ;
 * - QCM (entités, artéfacts) et Duel QCM : essai unique ;
 * - Lieux et Liste : une entrée trouvée (pas de notion de 2e essai).
 */
@RunWith(AndroidJUnit4::class)
class GoodAnswerFlowTest {

    // ── Quiz Classique ──────────────────────────────────────────────────────

    private fun classicGoodAnswer(
        domainButton: Int, choiceDest: Int, quizDest: Int, resultDest: Int, graphId: Int,
        isArtifact: Boolean, secondTry: Boolean,
    ) {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(R.id.btn_primary_2)).perform(click()) // Classique
        onView(withId(domainButton)).perform(click())
        scenario.assertCurrentDestination(choiceDest)
        onView(withId(R.id.btn_level_easy)).perform(click())
        scenario.waitForDestination(quizDest)

        val vm = scenario.graphVm<QuizViewModel>(graphId)
        fun currentName(): String? = scenario.onMain {
            val i = vm.currentIndex.value ?: 0
            if (isArtifact) vm.quizArtifacts.value?.getOrNull(i)?.name
            else vm.quizEntites.value?.getOrNull(i)?.name
        }
        waitFor { currentName() != null }
        val name = currentName()!!

        if (secondTry) {
            answerAndValidate(WRONG, R.id.et_answer, R.id.btn_validate, scrollable = true)
            waitFor { scenario.onMain { vm.currentStep.value } == 2 }
        }
        answerAndValidate(name, R.id.et_answer, R.id.btn_validate, scrollable = true)
        scenario.waitForDestination(resultDest)

        val expected = if (secondTry) "yellow" else "green"
        assertEquals("Statut de la question « $name »", expected, scenario.onMain { vm.results.value?.getOrNull(0) })
        assertTrue("Des points doivent être accordés", scenario.onMain { vm.score.value ?: 0.0 } > 0.0)
        scenario.close()
    }

    @Test
    fun entityClassicGoodAnswerFirstTry() = classicGoodAnswer(
        R.id.btn_primary_1, R.id.quizEntityChoiceFragment, R.id.quizEntityFragment,
        R.id.quizEntityResultFragment, R.id.quiz_entity_graph, isArtifact = false, secondTry = false
    )

    @Test
    fun entityClassicGoodAnswerSecondTry() = classicGoodAnswer(
        R.id.btn_primary_1, R.id.quizEntityChoiceFragment, R.id.quizEntityFragment,
        R.id.quizEntityResultFragment, R.id.quiz_entity_graph, isArtifact = false, secondTry = true
    )

    @Test
    fun artifactClassicGoodAnswerFirstTry() = classicGoodAnswer(
        R.id.btn_primary_2, R.id.quizArtifactChoiceFragment, R.id.quizArtifactFragment,
        R.id.quizArtifactResultFragment, R.id.quiz_artifact_graph, isArtifact = true, secondTry = false
    )

    @Test
    fun artifactClassicGoodAnswerSecondTry() = classicGoodAnswer(
        R.id.btn_primary_2, R.id.quizArtifactChoiceFragment, R.id.quizArtifactFragment,
        R.id.quizArtifactResultFragment, R.id.quiz_artifact_graph, isArtifact = true, secondTry = true
    )

    // ── QCM ─────────────────────────────────────────────────────────────────

    private fun qcmGoodAnswer(
        domainButton: Int, choiceDest: Int, quizDest: Int, resultDest: Int, graphId: Int, isArtifact: Boolean,
    ) {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(R.id.btn_primary_1)).perform(click()) // QCM
        onView(withId(domainButton)).perform(click())
        scenario.assertCurrentDestination(choiceDest)
        onView(withId(R.id.btn_level_easy)).perform(click())
        scenario.waitForDestination(quizDest)

        val vm = scenario.graphVm<QuizViewModel>(graphId)
        fun currentName(): String? = scenario.onMain {
            val i = vm.qcmIndex.value ?: 0
            if (isArtifact) vm.qcmArtifacts.value?.getOrNull(i)?.name
            else vm.qcmEntites.value?.getOrNull(i)?.name
        }
        waitFor { currentName() != null && scenario.onMain { vm.qcmChoices.value }.orEmpty().isNotEmpty() }
        val name = currentName()!!
        val index = scenario.onMain { vm.qcmChoices.value }.orEmpty().indexOf(name)
        assertTrue("La bonne réponse « $name » doit figurer parmi les choix", index in QCM_BUTTONS.indices)

        onView(withId(QCM_BUTTONS[index])).perform(click())
        scenario.waitForDestination(resultDest)

        assertEquals("Statut de la question « $name »", "green", scenario.onMain { vm.qcmResults.value?.getOrNull(0) })
        assertTrue("Des points doivent être accordés", scenario.onMain { vm.qcmScore.value ?: 0.0 } > 0.0)
        scenario.close()
    }

    @Test
    fun entityQcmGoodAnswer() = qcmGoodAnswer(
        R.id.btn_primary_1, R.id.quizEntityQcmChoiceFragment, R.id.quizEntityQcmFragment,
        R.id.quizEntityQcmResultFragment, R.id.quiz_entity_qcm_graph, isArtifact = false
    )

    @Test
    fun artifactQcmGoodAnswer() = qcmGoodAnswer(
        R.id.btn_primary_2, R.id.quizArtifactQcmChoiceFragment, R.id.quizArtifactQcmFragment,
        R.id.quizArtifactQcmResultFragment, R.id.quiz_artifact_qcm_graph, isArtifact = true
    )

    // ── Duel ────────────────────────────────────────────────────────────────

    /** Mise en place d'un duel à 2 joueurs (noms par défaut, niveau facile, mêmes questions). */
    private fun startDuel(scenario: ActivityScenario<MainActivity>, modeButton: Int, questionDest: Int): DuelViewModel {
        onView(withId(R.id.btn_primary_3)).perform(click()) // Duel
        scenario.waitForDestination(R.id.duelPlayerCountFragment)
        onView(withId(R.id.btn_duel_players_next)).perform(click())
        onView(withId(R.id.btn_duel_names_next)).perform(click())
        onView(withId(modeButton)).perform(click())
        onView(withId(R.id.btn_level_easy)).perform(click())
        onView(withId(R.id.btn_duel_pool_same)).perform(click())
        scenario.waitForDestination(R.id.duelAnnounceFragment)
        onView(withId(R.id.btn_duel_announce_start)).perform(click())
        scenario.waitForDestination(questionDest)
        return scenario.graphVm(R.id.duel_graph)
    }

    private fun duelClassicGoodAnswer(secondTry: Boolean) {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        val vm = startDuel(scenario, R.id.btn_primary_1, R.id.duelQuestionClassicFragment)
        val name = scenario.onMain { vm.currentQuestion()?.name }
        assertNotNull("Le duel doit avoir une question courante", name)

        if (secondTry) {
            answerAndValidate(WRONG, R.id.et_answer, R.id.btn_validate, scrollable = true)
            waitFor { scenario.onMain { vm.currentStep.value } == 2 }
        }
        answerAndValidate(name!!, R.id.et_answer, R.id.btn_validate, scrollable = true)
        scenario.waitForDestination(R.id.duelRecapFragment)

        val player = scenario.onMain { vm.players.value!!.first() }
        assertEquals("Statut de la question « $name »", if (secondTry) "yellow" else "green", player.results[0])
        assertTrue("Des points doivent être accordés", player.score > 0.0)
        scenario.close()
    }

    @Test
    fun duelClassicGoodAnswerFirstTry() = duelClassicGoodAnswer(secondTry = false)

    @Test
    fun duelClassicGoodAnswerSecondTry() = duelClassicGoodAnswer(secondTry = true)

    @Test
    fun duelQcmGoodAnswer() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        val vm = startDuel(scenario, R.id.btn_primary_2, R.id.duelQuestionQcmFragment)
        waitFor { scenario.onMain { vm.qcmChoices.value }.orEmpty().isNotEmpty() }
        val name = scenario.onMain { vm.currentQuestion()?.name }!!
        val index = scenario.onMain { vm.qcmChoices.value }.orEmpty().indexOf(name)
        assertTrue("La bonne réponse « $name » doit figurer parmi les choix", index in QCM_BUTTONS.indices)

        onView(withId(QCM_BUTTONS[index])).perform(click())
        scenario.waitForDestination(R.id.duelRecapFragment)

        val player = scenario.onMain { vm.players.value!!.first() }
        assertEquals("Statut de la question « $name »", "green", player.results[0])
        assertTrue("Des points doivent être accordés", player.score > 0.0)
        scenario.close()
    }

    // ── Lieux et Liste ──────────────────────────────────────────────────────

    @Test
    fun placeQuizGoodAnswerMarksPlaceFound() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(R.id.btn_primary_2)).perform(click()) // Classique
        onView(withId(R.id.btn_primary_3)).perform(click()) // Lieux
        onView(withId(R.id.btn_primary_1)).perform(click()) // Arbre Monde
        scenario.waitForDestination(R.id.quizYggdrasilFragment)

        val vm = scenario.currentFragmentQuizVm()
        waitFor { scenario.onMain { vm.yggdrasilRealms.value }.orEmpty().isNotEmpty() }
        val place = scenario.onMain { vm.yggdrasilRealms.value!!.first() }

        answerAndValidate(place.name, R.id.et_place_answer, R.id.btn_place_validate, scrollable = false)
        waitFor { place.id in scenario.onMain { vm.foundIds.value }.orEmpty() }
        assertEquals("Une bonne réponse ne compte pas comme erreur", 0, scenario.onMain { vm.placeWrongAttempts.value })
        scenario.close()
    }

    @Test
    fun listQuizGoodAnswerMarksItemFound() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(R.id.btn_primary_3)).perform(click()) // Liste
        onView(withId(R.id.container_themes)).perform(clickFirstChildButton())
        scenario.waitForDestination(R.id.quizListFragment)

        val vm = scenario.currentFragmentQuizVm()
        waitFor { scenario.onMain { vm.allListItems() }.isNotEmpty() }
        val item = scenario.onMain { vm.allListItems().first() }

        answerAndValidate(item.name, R.id.et_list_answer, R.id.btn_list_validate, scrollable = false)
        waitFor { item.id in scenario.onMain { vm.listFoundIds.value }.orEmpty() }
        assertEquals("Une bonne réponse ne compte pas comme erreur", 0, scenario.onMain { vm.listWrongAttempts.value })
        scenario.close()
    }
}
