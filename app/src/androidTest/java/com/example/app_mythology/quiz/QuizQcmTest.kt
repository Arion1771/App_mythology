package com.example.app_mythology.quiz

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
 * Quiz QCM (entités, artéfacts) — branche Test-Non-Regression : quatre choix
 * affichés, bonne réponse (statut green, points accordés) et mauvaise réponse
 * (statut red, aucun point), chacune menant à l'écran de résultat, puis QCM
 * complet joué jusqu'à l'écran de score avec vérification du score final.
 * Essai unique : pas de notion de 2e essai.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class QuizQcmTest {

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

    /** Répond à la première question : bonne réponse si [correct], sinon un leurre. */
    private fun answer(domain: Domain, correct: Boolean) {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(R.id.btn_primary_1)).perform(click()) // QCM
        onView(withId(domain.button)).perform(click())
        scenario.assertCurrentDestination(domain.choiceDest)
        onView(withId(R.id.btn_level_easy)).perform(click())
        scenario.waitForDestination(domain.quizDest)
        QCM_CHOICE_BUTTONS.forEach { onView(withId(it)).check(matches(isDisplayed())) }

        val vm = scenario.graphViewModel<QuizViewModel>(domain.graphId)
        fun currentName(): String? = scenario.onMain {
            val i = vm.qcmIndex.value ?: 0
            if (domain.isArtifact) vm.qcmArtifacts.value?.getOrNull(i)?.name
            else vm.qcmEntites.value?.getOrNull(i)?.name
        }
        waitFor { currentName() != null && scenario.onMain { vm.qcmChoices.value }.orEmpty().isNotEmpty() }
        val name = currentName()!!
        val choices = scenario.onMain { vm.qcmChoices.value }.orEmpty()
        val index = if (correct) choices.indexOf(name) else choices.indexOfFirst { it != name }
        assertTrue("Choix introuvable pour « $name » parmi $choices", index in QCM_CHOICE_BUTTONS.indices)

        onView(withId(QCM_CHOICE_BUTTONS[index])).perform(click())
        scenario.waitForDestination(domain.resultDest)

        val score = scenario.onMain { vm.qcmScore.value ?: 0.0 }
        assertEquals("Statut de la question « $name »", if (correct) "green" else "red",
            scenario.onMain { vm.qcmResults.value?.getOrNull(0) })
        if (correct) assertTrue("Des points doivent être accordés", score > 0.0)
        else assertEquals("Aucun point pour une mauvaise réponse", 0.0, score, 0.0)
        scenario.close()
    }

    @Test
    fun t01_entityGoodAnswer() = answer(entities, correct = true)

    @Test
    fun t02_entityWrongAnswer() = answer(entities, correct = false)

    @Test
    fun t03_artifactGoodAnswer() = answer(artifacts, correct = true)

    @Test
    fun t04_artifactWrongAnswer() = answer(artifacts, correct = false)

    /**
     * Joue tout le QCM en alternant bonne réponse (points pleins) et mauvaise
     * réponse (0), puis vérifie le score final calculé et celui affiché.
     */
    private fun fullQcmScore(domain: Domain) {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(R.id.btn_primary_1)).perform(click()) // QCM
        onView(withId(domain.button)).perform(click())
        onView(withId(R.id.btn_level_easy)).perform(click())
        scenario.waitForDestination(domain.quizDest)

        val vm = scenario.graphViewModel<QuizViewModel>(domain.graphId)
        waitFor { scenario.onMain { vm.qcmResults.value }.orEmpty().isNotEmpty() }
        val size = scenario.onMain { vm.qcmResults.value!!.size }

        var expected = 0.0
        val expectedResults = mutableListOf<String>()
        for (i in 0 until size) {
            scenario.waitForDestination(domain.quizDest)
            val (name, points) = scenario.onMain {
                if (domain.isArtifact) vm.qcmArtifacts.value!![i].let { it.name to it.difficulty.toDouble() }
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
        onView(withId(R.id.layout_result)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_score)).check(
            matches(withText("Score : ${formatScoreLikeApp(expected)} / ${formatScoreLikeApp(max)}"))
        )
        scenario.close()
    }

    @Test
    fun t05_entityFullQcmScore() = fullQcmScore(entities)

    @Test
    fun t06_artifactFullQcmScore() = fullQcmScore(artifacts)
}
