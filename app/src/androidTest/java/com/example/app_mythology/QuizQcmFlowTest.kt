package com.example.app_mythology

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.ui.MainActivity
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Déroulé du quiz QCM (Entités et Artéfacts) : choix de la difficulté, 4
 * propositions affichées, un clic (juste ou faux, aléatoire) bascule
 * directement vers l'écran de résultat de la question (branche
 * Test-Non-Regression).
 */
@RunWith(AndroidJUnit4::class)
class QuizQcmFlowTest {

    private fun goToQcmDomainChoice() {
        onView(withId(R.id.btn_primary_2)).perform(click()) // Home -> quizChoice
        onView(withId(R.id.btn_primary_1)).perform(click()) // quizChoice -> qcmDomainChoice
    }

    @Test
    fun entityQcmChoiceAnswerNavigatesToResult() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        goToQcmDomainChoice()
        onView(withId(R.id.btn_primary_1)).perform(click()) // -> quizEntityQcmChoiceFragment
        scenario.assertCurrentDestination(R.id.quizEntityQcmChoiceFragment)

        onView(withId(R.id.btn_level_easy)).perform(click())
        scenario.waitForDestination(R.id.quizEntityQcmFragment)

        onView(withId(R.id.btn_qcm_choice_0)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_qcm_choice_1)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_qcm_choice_2)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_qcm_choice_3)).check(matches(isDisplayed()))

        onView(withId(R.id.btn_qcm_choice_0)).perform(click())
        scenario.assertCurrentDestination(R.id.quizEntityQcmResultFragment)

        scenario.close()
    }

    @Test
    fun artifactQcmChoiceAnswerNavigatesToResult() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        goToQcmDomainChoice()
        onView(withId(R.id.btn_primary_2)).perform(click()) // -> quizArtifactQcmChoiceFragment
        scenario.assertCurrentDestination(R.id.quizArtifactQcmChoiceFragment)

        onView(withId(R.id.btn_level_easy)).perform(click())
        scenario.waitForDestination(R.id.quizArtifactQcmFragment)

        onView(withId(R.id.btn_qcm_choice_0)).perform(click())
        scenario.assertCurrentDestination(R.id.quizArtifactQcmResultFragment)

        scenario.close()
    }
}
