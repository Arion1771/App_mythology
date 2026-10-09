package com.example.app_mythology.quiz

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.R
import com.example.app_mythology.WRONG_ANSWER
import com.example.app_mythology.answerAndValidate
import com.example.app_mythology.assertCurrentDestination
import com.example.app_mythology.graphViewModel
import com.example.app_mythology.onMain
import com.example.app_mythology.ui.MainActivity
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
 * Quiz Classique (entités, artéfacts) — branche Test-Non-Regression : essai
 * vide ignoré, essai faux révélant les informations complémentaires, bonne
 * réponse au 1er essai (statut green) et au 2e essai (statut yellow), avec
 * points accordés et passage à l'écran de résultat. Les questions étant
 * tirées au hasard, la bonne réponse est lue dans le ViewModel du quiz.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class QuizClassicTest {

    private class Domain(
        val button: Int, val choiceDest: Int, val quizDest: Int, val resultDest: Int,
        val graphId: Int, val isArtifact: Boolean,
    )

    private val entities = Domain(
        R.id.btn_primary_1, R.id.quizEntityChoiceFragment, R.id.quizEntityFragment,
        R.id.quizEntityResultFragment, R.id.quiz_entity_graph, isArtifact = false
    )
    private val artifacts = Domain(
        R.id.btn_primary_2, R.id.quizArtifactChoiceFragment, R.id.quizArtifactFragment,
        R.id.quizArtifactResultFragment, R.id.quiz_artifact_graph, isArtifact = true
    )

    /** Ouvre la première question du domaine (niveau facile). */
    private fun openFirstQuestion(domain: Domain): ActivityScenario<MainActivity> {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(R.id.btn_primary_2)).perform(click()) // Classique
        onView(withId(domain.button)).perform(click())
        scenario.assertCurrentDestination(domain.choiceDest)
        onView(withId(R.id.btn_level_easy)).perform(click())
        scenario.waitForDestination(domain.quizDest)
        onView(withId(R.id.tv_quiz_progress)).check(matches(isDisplayed()))
        onView(withId(R.id.et_answer)).check(matches(isDisplayed()))
        return scenario
    }

    private fun goodAnswer(domain: Domain, secondTry: Boolean) {
        val scenario = openFirstQuestion(domain)
        val vm = scenario.graphViewModel<QuizViewModel>(domain.graphId)
        fun currentName(): String? = scenario.onMain {
            val i = vm.currentIndex.value ?: 0
            if (domain.isArtifact) vm.quizArtifacts.value?.getOrNull(i)?.name
            else vm.quizEntites.value?.getOrNull(i)?.name
        }
        waitFor { currentName() != null }
        val name = currentName()!!

        if (secondTry) {
            answerAndValidate(WRONG_ANSWER, R.id.et_answer, R.id.btn_validate, scrollable = true)
            waitFor { scenario.onMain { vm.currentStep.value } == 2 }
        }
        answerAndValidate(name, R.id.et_answer, R.id.btn_validate, scrollable = true)
        scenario.waitForDestination(domain.resultDest)

        val expected = if (secondTry) "yellow" else "green"
        assertEquals("Statut de la question « $name »", expected, scenario.onMain { vm.results.value?.getOrNull(0) })
        assertTrue("Des points doivent être accordés", scenario.onMain { vm.score.value ?: 0.0 } > 0.0)
        scenario.close()
    }

    @Test
    fun t01_entityEmptyThenWrongAnswerRevealsInfoPanel() {
        val scenario = openFirstQuestion(entities)

        // Essai vide : ignoré, toujours sur la question 1.
        onView(withId(R.id.btn_validate)).perform(scrollTo(), click())
        onView(withId(R.id.group_all_info)).check(matches(withEffectiveVisibility(Visibility.GONE)))

        // Essai faux : bascule au 2e essai, les informations complémentaires apparaissent.
        answerAndValidate(WRONG_ANSWER, R.id.et_answer, R.id.btn_validate, scrollable = true)
        onView(withId(R.id.group_all_info)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun t02_entityGoodAnswerFirstTry() = goodAnswer(entities, secondTry = false)

    @Test
    fun t03_entityGoodAnswerSecondTry() = goodAnswer(entities, secondTry = true)

    @Test
    fun t04_artifactGoodAnswerFirstTry() = goodAnswer(artifacts, secondTry = false)

    @Test
    fun t05_artifactGoodAnswerSecondTry() = goodAnswer(artifacts, secondTry = true)
}
