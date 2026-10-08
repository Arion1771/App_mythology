package com.example.app_mythology

import android.view.ViewGroup
import android.widget.Button
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.ui.MainActivity
import org.hamcrest.Matcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Test
import org.junit.runner.RunWith

/** Clique le premier bouton enfant d'un conteneur créé dynamiquement (thèmes du mode Liste). */
private fun clickFirstChildButton(): ViewAction = object : ViewAction {
    override fun getConstraints(): Matcher<android.view.View> = IsInstanceOf.instanceOf(ViewGroup::class.java)
    override fun getDescription() = "clique le premier bouton enfant"
    override fun perform(uiController: UiController, view: android.view.View) {
        val group = view as ViewGroup
        val first = (0 until group.childCount).map { group.getChildAt(it) }.first { it is Button }
        first.performClick()
        uiController.loopMainThreadUntilIdle()
    }
}

/**
 * Déroulé du quiz Liste (choix d'un thème, grille de cartes) et du quiz
 * Lieux (Arbre Monde / Fleuves de l'Enfer / Royaume des Morts) — branche
 * Test-Non-Regression.
 */
@RunWith(AndroidJUnit4::class)
class QuizListAndPlaceFlowTest {

    @Test
    fun pickingAnyListThemeOpensItsGridWithInputReady() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Home -> quizChoice
        onView(withId(R.id.btn_primary_3)).perform(click()) // quizChoice -> quizListChoice
        scenario.assertCurrentDestination(R.id.quizListChoiceFragment)

        onView(withId(R.id.container_themes)).perform(clickFirstChildButton())
        scenario.waitForDestination(R.id.quizListFragment)

        onView(withId(R.id.tv_list_quiz_title)).check(matches(isDisplayed()))
        onView(withId(R.id.et_list_answer)).check(matches(isDisplayed()))

        pressBack()
        scenario.assertCurrentDestination(R.id.quizListChoiceFragment)
        scenario.close()
    }

    @Test
    fun placeQuizScreensShowTheirGridAndInput() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // quizChoice
        onView(withId(R.id.btn_primary_2)).perform(click()) // classicDomainChoice
        onView(withId(R.id.btn_primary_3)).perform(click()) // quizPlaceChoice
        scenario.assertCurrentDestination(R.id.quizPlaceChoiceFragment)

        onView(withId(R.id.btn_primary_1)).perform(click()) // Arbre Monde
        scenario.assertCurrentDestination(R.id.quizYggdrasilFragment)
        onView(withId(R.id.grid_places)).check(matches(isDisplayed()))
        onView(withId(R.id.et_place_answer)).check(matches(isDisplayed()))
        pressBack()

        onView(withId(R.id.btn_primary_2)).perform(click()) // Fleuves de l'Enfer
        scenario.assertCurrentDestination(R.id.quizRiversFragment)
        onView(withId(R.id.grid_places)).check(matches(isDisplayed()))
        pressBack()

        onView(withId(R.id.btn_primary_3)).perform(click()) // Royaume des Morts
        scenario.assertCurrentDestination(R.id.quizUnderworldFragment)
        onView(withId(R.id.grid_places)).check(matches(isDisplayed()))
        pressBack()

        scenario.close()
    }
}
