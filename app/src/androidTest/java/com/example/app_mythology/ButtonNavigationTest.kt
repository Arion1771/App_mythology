package com.example.app_mythology

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.ui.MainActivity
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Vérifie que chaque bouton principal (colonne `view_primary_buttons`, slots
 * `btn_primary_1/2/3`) de chaque écran de menu, ainsi que les boutons isolés
 * (trophée, aide quiz), navigue bien vers l'écran attendu, et que le retour
 * système ramène à l'écran précédent (branche Test-Non-Regression).
 */
@RunWith(AndroidJUnit4::class)
class ButtonNavigationTest {

    @Test
    fun homeButtonsNavigateAndBackReturnsHome() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.assertCurrentDestination(R.id.homeFragment)

        onView(withId(R.id.btn_primary_1)).perform(click()) // Données
        scenario.assertCurrentDestination(R.id.browseChoiceFragment)
        pressBack()
        scenario.assertCurrentDestination(R.id.homeFragment)

        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        scenario.assertCurrentDestination(R.id.quizChoiceFragment)
        pressBack()
        scenario.assertCurrentDestination(R.id.homeFragment)

        onView(withId(R.id.btn_primary_3)).perform(click()) // Duel
        scenario.waitForDestination(R.id.duelPlayerCountFragment)
        pressBack()
        scenario.assertCurrentDestination(R.id.homeFragment)

        scenario.close()
    }

    @Test
    fun homeTrophyButtonOpensAchievements() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_trophy)).perform(click())
        scenario.assertCurrentDestination(R.id.achievementsFragment)
        pressBack()
        scenario.assertCurrentDestination(R.id.homeFragment)
        scenario.close()
    }

    @Test
    fun browseChoiceButtonsNavigateToEachList() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_1)).perform(click()) // -> browseChoiceFragment
        scenario.assertCurrentDestination(R.id.browseChoiceFragment)

        onView(withId(R.id.btn_primary_1)).perform(click()) // Entités
        scenario.assertCurrentDestination(R.id.entityListFragment)
        pressBack()
        scenario.assertCurrentDestination(R.id.browseChoiceFragment)

        onView(withId(R.id.btn_primary_2)).perform(click()) // Lieux
        scenario.assertCurrentDestination(R.id.placeListFragment)
        pressBack()
        scenario.assertCurrentDestination(R.id.browseChoiceFragment)

        onView(withId(R.id.btn_primary_3)).perform(click()) // Artéfacts
        scenario.assertCurrentDestination(R.id.artifactListFragment)
        pressBack()
        scenario.assertCurrentDestination(R.id.browseChoiceFragment)

        scenario.close()
    }

    @Test
    fun quizChoiceButtonsAndHelpNavigate() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // -> quizChoiceFragment
        scenario.assertCurrentDestination(R.id.quizChoiceFragment)

        onView(withId(R.id.btn_primary_1)).perform(click()) // QCM
        scenario.assertCurrentDestination(R.id.qcmDomainChoiceFragment)
        pressBack()
        scenario.assertCurrentDestination(R.id.quizChoiceFragment)

        onView(withId(R.id.btn_primary_2)).perform(click()) // Classique
        scenario.assertCurrentDestination(R.id.classicDomainChoiceFragment)
        pressBack()
        scenario.assertCurrentDestination(R.id.quizChoiceFragment)

        onView(withId(R.id.btn_primary_3)).perform(click()) // Liste
        scenario.assertCurrentDestination(R.id.quizListChoiceFragment)
        pressBack()
        scenario.assertCurrentDestination(R.id.quizChoiceFragment)

        onView(withId(R.id.btn_quiz_help)).perform(click())
        scenario.assertCurrentDestination(R.id.quizHelpFragment)
        pressBack()
        scenario.assertCurrentDestination(R.id.quizChoiceFragment)

        scenario.close()
    }

    @Test
    fun classicDomainChoiceHasThreeDomainsIncludingPlaces() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // quizChoice
        onView(withId(R.id.btn_primary_2)).perform(click()) // classicDomainChoice
        scenario.assertCurrentDestination(R.id.classicDomainChoiceFragment)

        onView(withId(R.id.btn_primary_1)).perform(click()) // Entités
        scenario.assertCurrentDestination(R.id.quizEntityChoiceFragment)
        pressBack()

        onView(withId(R.id.btn_primary_2)).perform(click()) // Artéfacts
        scenario.assertCurrentDestination(R.id.quizArtifactChoiceFragment)
        pressBack()

        onView(withId(R.id.btn_primary_3)).perform(click()) // Lieux
        scenario.assertCurrentDestination(R.id.quizPlaceChoiceFragment)
        pressBack()

        scenario.close()
    }

    @Test
    fun qcmDomainChoiceHasOnlyTwoDomains() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // quizChoice
        onView(withId(R.id.btn_primary_1)).perform(click()) // qcmDomainChoice
        scenario.assertCurrentDestination(R.id.qcmDomainChoiceFragment)

        onView(withId(R.id.btn_primary_1)).perform(click()) // Entités
        scenario.assertCurrentDestination(R.id.quizEntityQcmChoiceFragment)
        pressBack()

        onView(withId(R.id.btn_primary_2)).perform(click()) // Artéfacts
        scenario.assertCurrentDestination(R.id.quizArtifactQcmChoiceFragment)
        pressBack()

        scenario.close()
    }

    @Test
    fun placeChoiceButtonsNavigateToEachPlaceQuiz() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // quizChoice
        onView(withId(R.id.btn_primary_2)).perform(click()) // classicDomainChoice
        onView(withId(R.id.btn_primary_3)).perform(click()) // quizPlaceChoice
        scenario.assertCurrentDestination(R.id.quizPlaceChoiceFragment)

        onView(withId(R.id.btn_primary_1)).perform(click()) // Arbre Monde
        scenario.assertCurrentDestination(R.id.quizYggdrasilFragment)
        pressBack()

        onView(withId(R.id.btn_primary_2)).perform(click()) // Fleuves de l'Enfer
        scenario.assertCurrentDestination(R.id.quizRiversFragment)
        pressBack()

        onView(withId(R.id.btn_primary_3)).perform(click()) // Royaume des Morts
        scenario.assertCurrentDestination(R.id.quizUnderworldFragment)
        pressBack()

        scenario.close()
    }
}
