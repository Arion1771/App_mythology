package com.example.app_mythology

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.ui.MainActivity
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Déroulé du quiz Classique (Entités et Artéfacts) : choix de la difficulté,
 * première question affichée, essai vide ignoré, essai faux révélant les
 * informations complémentaires (branche Test-Non-Regression). Le quiz tire
 * des entités au hasard : on ne peut pas garantir une bonne réponse, donc le
 * chemin testé est volontairement celui de la réponse fausse, déterministe.
 */
@RunWith(AndroidJUnit4::class)
class QuizClassicFlowTest {

    private fun goToQuizChoice() {
        onView(withId(R.id.btn_primary_2)).perform(click()) // Home -> quizChoice
        onView(withId(R.id.btn_primary_2)).perform(click()) // quizChoice -> classicDomainChoice
    }

    @Test
    fun entityClassicFirstWrongAnswerRevealsInfoPanel() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        goToQuizChoice()
        onView(withId(R.id.btn_primary_1)).perform(click()) // classicDomainChoice -> quizEntityChoice
        scenario.assertCurrentDestination(R.id.quizEntityChoiceFragment)

        onView(withId(R.id.btn_level_easy)).perform(click())
        scenario.waitForDestination(R.id.quizEntityFragment)

        onView(withId(R.id.tv_quiz_progress)).check(matches(isDisplayed()))

        // Essai vide : ignoré, toujours sur la question 1.
        onView(withId(R.id.btn_validate)).perform(scrollTo(), click())
        onView(withId(R.id.group_all_info)).check(matches(androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility(
            androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE)))

        // Essai faux : bascule au 2e essai, les informations complémentaires apparaissent.
        // Clavier fermé et défilement avant le clic : le clavier ouvert par la
        // saisie peut masquer « Valider » (Espresso exige 90 % de la vue visible).
        onView(withId(R.id.et_answer)).perform(typeText("__reponse_forcement_fausse__"))
        closeSoftKeyboard()
        onView(withId(R.id.btn_validate)).perform(scrollTo(), click())
        onView(withId(R.id.group_all_info)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun artifactClassicChoiceScreenReachableAndShowsFirstQuestion() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        goToQuizChoice()
        onView(withId(R.id.btn_primary_2)).perform(click()) // classicDomainChoice -> quizArtifactChoice
        scenario.assertCurrentDestination(R.id.quizArtifactChoiceFragment)

        onView(withId(R.id.btn_level_easy)).perform(click())
        scenario.waitForDestination(R.id.quizArtifactFragment)
        onView(withId(R.id.tv_quiz_progress)).check(matches(isDisplayed()))
        onView(withId(R.id.et_answer)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun backFromDifficultyChoiceReturnsToDomainChoice() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        goToQuizChoice()
        onView(withId(R.id.btn_primary_1)).perform(click())
        scenario.assertCurrentDestination(R.id.quizEntityChoiceFragment)
        pressBack()
        scenario.assertCurrentDestination(R.id.classicDomainChoiceFragment)
        scenario.close()
    }
}
