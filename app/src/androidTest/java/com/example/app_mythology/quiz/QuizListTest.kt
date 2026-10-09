package com.example.app_mythology.quiz

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.DeviceStateRestoreRule
import com.example.app_mythology.R
import com.example.app_mythology.WRONG_ANSWER
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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Quiz Liste (thème choisi, retrouver toutes les entrées) — branche
 * Test-Non-Regression : le choix d'un thème ouvre sa grille de cartes avec
 * la saisie prête, une bonne réponse marque l'entrée comme trouvée sans
 * compter d'erreur, et un thème complet se termine sur le score attendu.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class QuizListTest {

    /** Rencontres, niveaux internes et succès de l'appareil restaurés après chaque test. */
    @get:Rule
    val deviceState = DeviceStateRestoreRule()

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

    @Test
    fun t03_fullThemeShowsScore() {
        val scenario = openFirstTheme()
        val vm = scenario.currentFragmentQuizViewModel()
        waitFor { scenario.onMain { vm.allListItems() }.isNotEmpty() }
        val items = scenario.onMain { vm.allListItems() }

        // Une erreur seulement si le thème en tolère plus d'une (sinon elle terminerait le quiz).
        val withError = scenario.onMain { vm.currentListMaxErrors() } > 1
        if (withError) answerAndValidate(WRONG_ANSWER, R.id.et_list_answer, R.id.btn_list_validate, scrollable = false)
        items.forEach { answerAndValidate(it.name, R.id.et_list_answer, R.id.btn_list_validate, scrollable = false) }

        waitFor { scenario.onMain { vm.listFinished.value } == true }
        assertEquals("Toutes les entrées trouvées", items.map { it.id }.toSet(), scenario.onMain { vm.listFoundIds.value })
        assertEquals("Erreurs comptées", if (withError) 1 else 0, scenario.onMain { vm.listWrongAttempts.value })
        onView(withId(R.id.tv_list_score)).check(matches(withText("Score : ${items.size} / ${items.size} trouvés")))
        scenario.close()
    }
}
