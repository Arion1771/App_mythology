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
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.DeviceStateRestoreRule
import com.example.app_mythology.R
import com.example.app_mythology.WRONG_ANSWER
import com.example.app_mythology.answerAndValidate
import com.example.app_mythology.assertCurrentDestination
import com.example.app_mythology.formatScoreLikeApp
import com.example.app_mythology.graphViewModel
import com.example.app_mythology.onMain
import com.example.app_mythology.ui.MainActivity
import com.example.app_mythology.viewmodel.QuizViewModel
import com.example.app_mythology.waitFor
import com.example.app_mythology.waitForDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Quiz Classique (entités, artéfacts) — branche Test-Non-Regression : essai
 * vide ignoré, essai faux révélant les informations complémentaires, bonne
 * réponse au 1er essai (statut green) et au 2e essai (statut yellow), avec
 * points accordés et passage à l'écran de résultat, puis quiz complet joué
 * jusqu'à l'écran de score avec vérification du score final. Les questions étant
 * tirées au hasard, la bonne réponse est lue dans le ViewModel du quiz.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class QuizClassicTest {

    /** Rencontres, niveaux internes et succès de l'appareil restaurés après chaque test. */
    @get:Rule
    val deviceState = DeviceStateRestoreRule()

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

    /**
     * Joue tout le quiz en alternant bonne réponse au 1er essai (points pleins),
     * au 2e essai (moitié des points) et deux essais faux (0), puis vérifie le
     * score final calculé et celui affiché.
     */
    private fun fullQuizScore(domain: Domain) {
        val scenario = openFirstQuestion(domain)
        val vm = scenario.graphViewModel<QuizViewModel>(domain.graphId)
        waitFor { scenario.onMain { vm.results.value }.orEmpty().isNotEmpty() }
        val size = scenario.onMain { vm.results.value!!.size }

        var expected = 0.0
        val expectedResults = mutableListOf<String>()
        for (i in 0 until size) {
            scenario.waitForDestination(domain.quizDest)
            waitFor { scenario.onMain { vm.currentIndex.value == i && vm.currentStep.value == 1 } }
            val (name, points) = scenario.onMain {
                if (domain.isArtifact) vm.quizArtifacts.value!![i].let { it.name to it.difficulty.toDouble() }
                else vm.quizEntites.value!![i].name to vm.entityPointsLevelAt(i).toDouble()
            }
            val answers = when (i % 3) {
                0 -> listOf(name).also { expected += points; expectedResults += "green" }
                1 -> listOf(WRONG_ANSWER, name).also { expected += points / 2; expectedResults += "yellow" }
                else -> listOf(WRONG_ANSWER, WRONG_ANSWER).also { expectedResults += "red" }
            }
            answers.forEachIndexed { attempt, answer ->
                answerAndValidate(answer, R.id.et_answer, R.id.btn_validate, scrollable = true)
                if (attempt == 0 && answers.size == 2) waitFor { scenario.onMain { vm.currentStep.value } == 2 }
            }
            scenario.waitForDestination(domain.resultDest)
            onView(withId(R.id.btn_result_next)).perform(scrollTo(), click()) // suivante / voir le score
        }

        scenario.waitForDestination(domain.quizDest)
        waitFor { scenario.onMain { vm.quizFinished.value } == true }
        assertEquals("Statuts de toutes les questions", expectedResults, scenario.onMain { vm.results.value })
        assertEquals("Score final", expected, scenario.onMain { vm.score.value ?: 0.0 }, 1e-9)
        val max = scenario.onMain { vm.maxScore.value ?: 0.0 }
        onView(withId(R.id.layout_result)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_score)).check(
            matches(withText("Score : ${formatScoreLikeApp(expected)} / ${formatScoreLikeApp(max)}"))
        )
        scenario.close()
    }

    @Test
    fun t06_entityFullQuizScore() = fullQuizScore(entities)

    @Test
    fun t07_artifactFullQuizScore() = fullQuizScore(artifacts)
}
