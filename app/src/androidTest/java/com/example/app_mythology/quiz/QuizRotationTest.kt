package com.example.app_mythology.quiz

import android.content.ComponentName
import android.content.pm.ActivityInfo
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
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
import com.example.app_mythology.clickFirstChildButton
import com.example.app_mythology.currentFragmentQuizViewModel
import com.example.app_mythology.graphViewModel
import com.example.app_mythology.onMain
import com.example.app_mythology.rotateToLandscapeAndBack
import com.example.app_mythology.startDuel
import com.example.app_mythology.ui.MainActivity
import com.example.app_mythology.viewmodel.DuelViewModel
import com.example.app_mythology.viewmodel.QuizViewModel
import com.example.app_mythology.waitFor
import com.example.app_mythology.waitForDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Intégrité des quiz lors d'un passage paysage → portrait — branche
 * Test-Non-Regression. Dans une ancienne version, la recréation de l'activité
 * lors d'une rotation remettait le quiz à zéro (corrigé en V1.6.5), puis
 * l'application a été verrouillée en portrait (V2.5.0).
 *
 * Le premier test verrouille ce blocage ; les suivants forcent malgré tout la
 * rotation en cours de partie, dans chaque mode, et vérifient que rien n'est
 * perdu (écran, question, essai, choix proposés, résultats, entrées trouvées,
 * erreurs) et que la partie se termine normalement ensuite.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class QuizRotationTest {

    @Test
    fun t01_mainActivityIsLockedToPortrait() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val info = context.packageManager.getActivityInfo(ComponentName(context, MainActivity::class.java), 0)
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, info.screenOrientation)
    }

    // ── Quiz Classique ──────────────────────────────────────────────────────

    private fun classicKeepsStateAcrossRotation(
        domainButton: Int, quizDest: Int, resultDest: Int, graphId: Int, isArtifact: Boolean,
    ) {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(R.id.btn_primary_2)).perform(click()) // Classique
        onView(withId(domainButton)).perform(click())
        onView(withId(R.id.btn_level_easy)).perform(click())
        scenario.waitForDestination(quizDest)

        fun vm() = scenario.graphViewModel<QuizViewModel>(graphId)
        fun currentName(): String? = scenario.onMain {
            val v = vm(); val i = v.currentIndex.value ?: 0
            if (isArtifact) v.quizArtifacts.value?.getOrNull(i)?.name else v.quizEntites.value?.getOrNull(i)?.name
        }
        waitFor { currentName() != null }
        val name = currentName()!!

        // Progression en cours : 1er essai faux, on est au 2e essai.
        answerAndValidate(WRONG_ANSWER, R.id.et_answer, R.id.btn_validate, scrollable = true)
        waitFor { scenario.onMain { vm().currentStep.value } == 2 }
        val resultsBefore = scenario.onMain { vm().results.value }

        scenario.rotateToLandscapeAndBack()

        scenario.assertCurrentDestination(quizDest)
        assertEquals("Même question après rotation", name, currentName())
        assertEquals("Toujours au 2e essai après rotation", 2, scenario.onMain { vm().currentStep.value })
        assertEquals("Résultats conservés", resultsBefore, scenario.onMain { vm().results.value })
        onView(withId(R.id.group_all_info)).check(matches(isDisplayed()))

        // La partie continue normalement : bonne réponse au 2e essai.
        answerAndValidate(name, R.id.et_answer, R.id.btn_validate, scrollable = true)
        scenario.waitForDestination(resultDest)
        assertEquals("yellow", scenario.onMain { vm().results.value?.getOrNull(0) })
        scenario.close()
    }

    @Test
    fun t02_entityClassicKeepsStateAcrossRotation() = classicKeepsStateAcrossRotation(
        R.id.btn_primary_1, R.id.quizEntityFragment, R.id.quizEntityResultFragment,
        R.id.quiz_entity_graph, isArtifact = false
    )

    @Test
    fun t03_artifactClassicKeepsStateAcrossRotation() = classicKeepsStateAcrossRotation(
        R.id.btn_primary_2, R.id.quizArtifactFragment, R.id.quizArtifactResultFragment,
        R.id.quiz_artifact_graph, isArtifact = true
    )

    // ── QCM ─────────────────────────────────────────────────────────────────

    private fun qcmKeepsStateAcrossRotation(
        domainButton: Int, quizDest: Int, resultDest: Int, graphId: Int, isArtifact: Boolean,
    ) {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(R.id.btn_primary_1)).perform(click()) // QCM
        onView(withId(domainButton)).perform(click())
        onView(withId(R.id.btn_level_easy)).perform(click())
        scenario.waitForDestination(quizDest)

        fun vm() = scenario.graphViewModel<QuizViewModel>(graphId)
        fun currentName(): String? = scenario.onMain {
            val v = vm(); val i = v.qcmIndex.value ?: 0
            if (isArtifact) v.qcmArtifacts.value?.getOrNull(i)?.name else v.qcmEntites.value?.getOrNull(i)?.name
        }
        waitFor { currentName() != null && scenario.onMain { vm().qcmChoices.value }.orEmpty().isNotEmpty() }
        val name = currentName()!!
        val choices = scenario.onMain { vm().qcmChoices.value }

        scenario.rotateToLandscapeAndBack()

        scenario.assertCurrentDestination(quizDest)
        assertEquals("Même question après rotation", name, currentName())
        assertEquals("Mêmes choix, dans le même ordre", choices, scenario.onMain { vm().qcmChoices.value })

        val index = choices.orEmpty().indexOf(name)
        onView(withId(QCM_CHOICE_BUTTONS[index])).perform(click())
        scenario.waitForDestination(resultDest)
        assertEquals("green", scenario.onMain { vm().qcmResults.value?.getOrNull(0) })
        scenario.close()
    }

    @Test
    fun t04_entityQcmKeepsStateAcrossRotation() = qcmKeepsStateAcrossRotation(
        R.id.btn_primary_1, R.id.quizEntityQcmFragment, R.id.quizEntityQcmResultFragment,
        R.id.quiz_entity_qcm_graph, isArtifact = false
    )

    @Test
    fun t05_artifactQcmKeepsStateAcrossRotation() = qcmKeepsStateAcrossRotation(
        R.id.btn_primary_2, R.id.quizArtifactQcmFragment, R.id.quizArtifactQcmResultFragment,
        R.id.quiz_artifact_qcm_graph, isArtifact = true
    )

    // ── Lieux et Liste ──────────────────────────────────────────────────────

    @Test
    fun t06_placeQuizKeepsFoundPlacesAndErrorsAcrossRotation() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(R.id.btn_primary_2)).perform(click()) // Classique
        onView(withId(R.id.btn_primary_3)).perform(click()) // Lieux
        onView(withId(R.id.btn_primary_1)).perform(click()) // Arbre Monde
        scenario.waitForDestination(R.id.quizYggdrasilFragment)

        fun vm() = scenario.currentFragmentQuizViewModel()
        waitFor { scenario.onMain { vm().yggdrasilRealms.value }.orEmpty().size >= 2 }
        val (first, second) = scenario.onMain { vm().yggdrasilRealms.value!!.take(2) }

        answerAndValidate(first.name, R.id.et_place_answer, R.id.btn_place_validate, scrollable = false)
        answerAndValidate(WRONG_ANSWER, R.id.et_place_answer, R.id.btn_place_validate, scrollable = false)
        waitFor { scenario.onMain { vm().placeWrongAttempts.value } == 1 }
        val foundBefore = scenario.onMain { vm().foundIds.value }

        scenario.rotateToLandscapeAndBack()

        scenario.assertCurrentDestination(R.id.quizYggdrasilFragment)
        assertEquals("Lieux trouvés conservés", foundBefore, scenario.onMain { vm().foundIds.value })
        assertEquals("Erreurs conservées", 1, scenario.onMain { vm().placeWrongAttempts.value })

        answerAndValidate(second.name, R.id.et_place_answer, R.id.btn_place_validate, scrollable = false)
        waitFor { scenario.onMain { vm().foundIds.value }.orEmpty() == setOf(first.id, second.id) }
        scenario.close()
    }

    @Test
    fun t07_listQuizKeepsFoundItemsAndErrorsAcrossRotation() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(R.id.btn_primary_3)).perform(click()) // Liste
        onView(withId(R.id.container_themes)).perform(clickFirstChildButton())
        scenario.waitForDestination(R.id.quizListFragment)

        fun vm() = scenario.currentFragmentQuizViewModel()
        waitFor { scenario.onMain { vm().allListItems() }.size >= 2 }
        val (first, second) = scenario.onMain { vm().allListItems().take(2) }

        answerAndValidate(first.name, R.id.et_list_answer, R.id.btn_list_validate, scrollable = false)
        answerAndValidate(WRONG_ANSWER, R.id.et_list_answer, R.id.btn_list_validate, scrollable = false)
        waitFor { scenario.onMain { vm().listWrongAttempts.value } == 1 }
        val foundBefore = scenario.onMain { vm().listFoundIds.value }
        val itemsBefore = scenario.onMain { vm().allListItems() }

        scenario.rotateToLandscapeAndBack()

        scenario.assertCurrentDestination(R.id.quizListFragment)
        assertEquals("Même thème, mêmes entrées", itemsBefore, scenario.onMain { vm().allListItems() })
        assertEquals("Entrées trouvées conservées", foundBefore, scenario.onMain { vm().listFoundIds.value })
        assertEquals("Erreurs conservées", 1, scenario.onMain { vm().listWrongAttempts.value })

        answerAndValidate(second.name, R.id.et_list_answer, R.id.btn_list_validate, scrollable = false)
        waitFor { scenario.onMain { vm().listFoundIds.value }.orEmpty() == setOf(first.id, second.id) }
        scenario.close()
    }

    // ── Duel ────────────────────────────────────────────────────────────────

    /** Joueur, tour, question et essai courants du duel. */
    private fun ActivityScenario<MainActivity>.duelPosition(vm: () -> DuelViewModel) = onMain {
        val v = vm()
        listOf(v.currentPlayerIndex.value, v.currentRound.value, v.currentQuestion()?.name, v.currentStep.value)
    }

    @Test
    fun t08_classicDuelKeepsStateAcrossRotation() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        startDuel(scenario, R.id.btn_primary_1, R.id.btn_duel_pool_same, R.id.duelQuestionClassicFragment)
        fun vm() = scenario.graphViewModel<DuelViewModel>(R.id.duel_graph)
        val name = scenario.onMain { vm().currentQuestion()!!.name }

        answerAndValidate(WRONG_ANSWER, R.id.et_answer, R.id.btn_validate, scrollable = true)
        waitFor { scenario.onMain { vm().currentStep.value } == 2 }
        val positionBefore = scenario.duelPosition(::vm)

        scenario.rotateToLandscapeAndBack()

        scenario.assertCurrentDestination(R.id.duelQuestionClassicFragment)
        assertEquals("Même joueur, tour, question et essai", positionBefore, scenario.duelPosition(::vm))

        answerAndValidate(name, R.id.et_answer, R.id.btn_validate, scrollable = true)
        scenario.waitForDestination(R.id.duelRecapFragment)
        assertEquals("yellow", scenario.onMain { vm().players.value!!.first().results[0] })
        scenario.close()
    }

    @Test
    fun t09_qcmDuelKeepsStateAcrossRotation() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        startDuel(scenario, R.id.btn_primary_2, R.id.btn_duel_pool_different, R.id.duelQuestionQcmFragment)
        fun vm() = scenario.graphViewModel<DuelViewModel>(R.id.duel_graph)
        waitFor { scenario.onMain { vm().qcmChoices.value }.orEmpty().isNotEmpty() }
        val name = scenario.onMain { vm().currentQuestion()!!.name }
        val positionBefore = scenario.duelPosition(::vm)
        val choices = scenario.onMain { vm().qcmChoices.value }

        scenario.rotateToLandscapeAndBack()

        scenario.assertCurrentDestination(R.id.duelQuestionQcmFragment)
        assertEquals("Même joueur, tour et question", positionBefore, scenario.duelPosition(::vm))
        assertEquals("Mêmes choix, dans le même ordre", choices, scenario.onMain { vm().qcmChoices.value })

        val index = choices.orEmpty().indexOf(name)
        assertTrue(index in QCM_CHOICE_BUTTONS.indices)
        onView(withId(QCM_CHOICE_BUTTONS[index])).perform(click())
        scenario.waitForDestination(R.id.duelRecapFragment)
        assertEquals("green", scenario.onMain { vm().players.value!!.first().results[0] })
        scenario.close()
    }
}
