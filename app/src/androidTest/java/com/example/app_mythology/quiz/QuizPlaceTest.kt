package com.example.app_mythology.quiz

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.R
import com.example.app_mythology.answerAndValidate
import com.example.app_mythology.assertCurrentDestination
import com.example.app_mythology.currentFragmentQuizViewModel
import com.example.app_mythology.onMain
import com.example.app_mythology.ui.MainActivity
import com.example.app_mythology.waitFor
import com.example.app_mythology.waitForDestination
import org.junit.Assert.assertEquals
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Quiz Lieux (Arbre Monde / Fleuves de l'Enfer / Royaume des Morts) — branche
 * Test-Non-Regression : chaque quiz s'ouvre avec sa grille et sa saisie, et
 * une bonne réponse marque le lieu comme trouvé sans compter d'erreur.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class QuizPlaceTest {

    private fun openPlaceChoice(): ActivityScenario<MainActivity> {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(R.id.btn_primary_2)).perform(click()) // Classique
        onView(withId(R.id.btn_primary_3)).perform(click()) // Lieux
        scenario.assertCurrentDestination(R.id.quizPlaceChoiceFragment)
        return scenario
    }

    @Test
    fun t01_eachPlaceQuizOpensWithGridAndInput() {
        val scenario = openPlaceChoice()
        for ((button, destination) in listOf(
            R.id.btn_primary_1 to R.id.quizYggdrasilFragment,  // Arbre Monde
            R.id.btn_primary_2 to R.id.quizRiversFragment,     // Fleuves de l'Enfer
            R.id.btn_primary_3 to R.id.quizUnderworldFragment, // Royaume des Morts
        )) {
            onView(withId(button)).perform(click())
            scenario.assertCurrentDestination(destination)
            onView(withId(R.id.grid_places)).check(matches(isDisplayed()))
            onView(withId(R.id.et_place_answer)).check(matches(isDisplayed()))
            pressBack()
            scenario.assertCurrentDestination(R.id.quizPlaceChoiceFragment)
        }
        scenario.close()
    }

    @Test
    fun t02_goodAnswerMarksPlaceFound() {
        val scenario = openPlaceChoice()
        onView(withId(R.id.btn_primary_1)).perform(click()) // Arbre Monde
        scenario.waitForDestination(R.id.quizYggdrasilFragment)

        val vm = scenario.currentFragmentQuizViewModel()
        waitFor { scenario.onMain { vm.yggdrasilRealms.value }.orEmpty().isNotEmpty() }
        val place = scenario.onMain { vm.yggdrasilRealms.value!!.first() }

        answerAndValidate(place.name, R.id.et_place_answer, R.id.btn_place_validate, scrollable = false)
        waitFor { place.id in scenario.onMain { vm.foundIds.value }.orEmpty() }
        assertEquals("Une bonne réponse ne compte pas comme erreur", 0, scenario.onMain { vm.placeWrongAttempts.value })
        scenario.close()
    }
}
