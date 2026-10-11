package com.example.app_mythology.n3_quiz

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.DeviceStateRestoreRule
import com.example.app_mythology.QCM_CHOICE_BUTTONS
import com.example.app_mythology.R
import com.example.app_mythology.quiz.FlatQuizRules
import com.example.app_mythology.assertCurrentDestination
import com.example.app_mythology.checkLevelButtonLabels
import com.example.app_mythology.formatScoreLikeApp
import com.example.app_mythology.graphViewModel
import com.example.app_mythology.onMain
import com.example.app_mythology.ui.MainActivity
import com.example.app_mythology.viewmodel.QuizViewModel
import com.example.app_mythology.waitFor
import com.example.app_mythology.waitForDestination
import org.junit.Assert.assertEquals
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Quiz QCM (entités, artéfacts) — branche Test-Non-Regression : QCM complet
 * joué jusqu'à l'écran de score, en alternant bonne réponse (statut green,
 * points pleins) et mauvaise réponse (statut red, aucun point), avec
 * vérification des quatre choix affichés, du statut de chaque question et du
 * score final. Essai unique : pas de notion de 2e essai.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class Quiz1QcmTest {

    /** Rencontres, niveaux internes et succès de l'appareil restaurés après chaque test. */
    @get:Rule
    val deviceState = DeviceStateRestoreRule()

    private class Domain(
        val button: Int, val choiceDest: Int, val quizDest: Int, val resultDest: Int,
        val graphId: Int, val isArtifact: Boolean,
    )

    private val entities = Domain(
        R.id.btn_primary_1, R.id.quizEntityQcmChoiceFragment, R.id.quizEntityQcmFragment,
        R.id.quizEntityQcmResultFragment, R.id.quiz_entity_qcm_graph, isArtifact = false
    )
    private val artifacts = Domain(
        R.id.btn_primary_2, R.id.quizArtifactQcmChoiceFragment, R.id.quizArtifactQcmFragment,
        R.id.quizArtifactQcmResultFragment, R.id.quiz_artifact_qcm_graph, isArtifact = true
    )

    /** Ouvre la première question QCM du domaine (niveau [levelButton], facile par défaut). */
    private fun openQcm(domain: Domain, levelButton: Int = R.id.btn_level_easy): ActivityScenario<MainActivity> {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(R.id.btn_primary_1)).perform(click()) // QCM
        onView(withId(domain.button)).perform(click())
        scenario.assertCurrentDestination(domain.choiceDest)
        checkLevelButtonLabels()
        onView(withId(levelButton)).perform(click())
        scenario.waitForDestination(domain.quizDest)
        return scenario
    }

    /**
     * Joue tout le QCM en alternant bonne réponse (points pleins) et mauvaise
     * réponse (0) : vérifie les quatre choix affichés, le statut de chaque
     * question (green / red), puis le score final calculé et celui affiché.
     */
    private fun fullQcmScore(domain: Domain) {
        val scenario = openQcm(domain)
        QCM_CHOICE_BUTTONS.forEach { onView(withId(it)).check(matches(isDisplayed())) }

        val vm = scenario.graphViewModel<QuizViewModel>(domain.graphId)
        waitFor { scenario.onMain { vm.qcmResults.value }.orEmpty().isNotEmpty() }
        val size = scenario.onMain { vm.qcmResults.value!!.size }
        assertEquals("Nombre de questions", FlatQuizRules.QUESTIONS_PER_QUIZ, size)

        var expected = 0.0
        val expectedResults = mutableListOf<String>()
        for (i in 0 until size) {
            scenario.waitForDestination(domain.quizDest)
            val (name, points) = scenario.onMain {
                if (domain.isArtifact) vm.qcmArtifacts.value!![i].name to FlatQuizRules.POINTS_PER_QUESTION
                else vm.qcmEntites.value!![i].name to vm.qcmEntityPointsLevelAt(i).toDouble()
            }
            waitFor { scenario.onMain { vm.qcmIndex.value == i && vm.qcmChoices.value.orEmpty().contains(name) } }
            val choices = scenario.onMain { vm.qcmChoices.value!! }
            val correct = i % 2 == 0
            if (correct) { expected += points; expectedResults += "green" } else expectedResults += "red"
            val index = if (correct) choices.indexOf(name) else choices.indexOfFirst { it != name }
            onView(withId(QCM_CHOICE_BUTTONS[index])).perform(click())
            scenario.waitForDestination(domain.resultDest)
            onView(withId(R.id.btn_result_next)).perform(scrollTo(), click()) // suivante / voir le score
        }

        scenario.waitForDestination(domain.quizDest)
        waitFor { scenario.onMain { vm.qcmFinished.value } == true }
        assertEquals("Statuts de toutes les questions", expectedResults, scenario.onMain { vm.qcmResults.value })
        assertEquals("Score final", expected, scenario.onMain { vm.qcmScore.value ?: 0.0 }, 1e-9)
        val max = scenario.onMain { vm.qcmMaxScore.value ?: 0.0 }
        if (domain.isArtifact) {
            assertEquals("Questions du QCM Artéfacts", FlatQuizRules.QUESTIONS_PER_QUIZ, size)
            assertEquals("Score maximal du QCM Artéfacts", 30.0, max, 1e-9)
        }
        onView(withId(R.id.layout_result)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_score)).check(
            matches(withText("Score : ${formatScoreLikeApp(expected)} / ${formatScoreLikeApp(max)}"))
        )
        scenario.close()
    }

    @Test
    fun t01_entityFullQcm() = fullQcmScore(entities)

    @Test
    fun t02_artifactFullQcm() = fullQcmScore(artifacts)

    /** Entités comme artéfacts, chaque niveau propose exactement 15 questions. */
    @Test
    fun t03_everyLevelHasFifteenQuestions() {
        for (domain in listOf(entities, artifacts)) {
            for (level in listOf(R.id.btn_level_easy, R.id.btn_level_medium, R.id.btn_level_hard)) {
                val scenario = openQcm(domain, level)
                val vm = scenario.graphViewModel<QuizViewModel>(domain.graphId)
                waitFor { scenario.onMain { vm.qcmResults.value }.orEmpty().isNotEmpty() }
                val count = scenario.onMain {
                    if (domain.isArtifact) vm.qcmArtifacts.value!!.size else vm.qcmEntites.value!!.size
                }
                assertEquals("Questions (artéfacts=${domain.isArtifact}, niveau $level)", FlatQuizRules.QUESTIONS_PER_QUIZ, count)
                scenario.close()
            }
        }
    }
}
