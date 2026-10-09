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
import com.example.app_mythology.clickFirstChildButton
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
 * Quiz Liste (thème choisi, retrouver toutes les entrées) — branche
 * Test-Non-Regression : le choix d'un thème ouvre sa grille de cartes avec
 * la saisie prête, et une bonne réponse marque l'entrée comme trouvée sans
 * compter d'erreur.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class QuizListTest {

    private fun openFirstTheme(): ActivityScenario<MainActivity> {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(R.id.btn_primary_3)).perform(click()) // Liste
        scenario.assertCurrentDestination(R.id.quizListChoiceFragment)
        onView(withId(R.id.container_themes)).perform(clickFirstChildButton())
        scenario.waitForDestination(R.id.quizListFragment)
        return scenario
    }

    @Test
    fun t01_pickingAThemeOpensItsGridWithInputReady() {
        val scenario = openFirstTheme()
        onView(withId(R.id.tv_list_quiz_title)).check(matches(isDisplayed()))
        onView(withId(R.id.et_list_answer)).check(matches(isDisplayed()))
        pressBack()
        scenario.assertCurrentDestination(R.id.quizListChoiceFragment)
        scenario.close()
    }

    @Test
    fun t02_goodAnswerMarksItemFound() {
        val scenario = openFirstTheme()
        val vm = scenario.currentFragmentQuizViewModel()
        waitFor { scenario.onMain { vm.allListItems() }.isNotEmpty() }
        val item = scenario.onMain { vm.allListItems().first() }

        answerAndValidate(item.name, R.id.et_list_answer, R.id.btn_list_validate, scrollable = false)
        waitFor { item.id in scenario.onMain { vm.listFoundIds.value }.orEmpty() }
        assertEquals("Une bonne réponse ne compte pas comme erreur", 0, scenario.onMain { vm.listWrongAttempts.value })
        scenario.close()
    }
}
