package com.example.app_mythology.quiz

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.QCM_CHOICE_BUTTONS
import com.example.app_mythology.R
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
 * Quiz QCM (entités, artéfacts) — branche Test-Non-Regression : quatre choix
 * affichés, bonne réponse (statut green, points accordés) et mauvaise réponse
 * (statut red, aucun point), chacune menant à l'écran de résultat. Essai
 * unique : pas de notion de 2e essai.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class QuizQcmTest {

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
}
