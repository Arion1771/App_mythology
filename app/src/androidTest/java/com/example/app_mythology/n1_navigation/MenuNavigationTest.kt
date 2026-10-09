package com.example.app_mythology.n1_navigation

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.R
import com.example.app_mythology.assertCurrentDestination
import com.example.app_mythology.ui.MainActivity
import com.example.app_mythology.waitForDestination
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Navigation par les trois boutons principaux (colonne `view_primary_buttons`,
 * slots `btn_primary_1/2/3`) de chaque menu, et retour système vers l'écran
 * précédent — branche Test-Non-Regression. Les boutons d'angle (←, 🏆, ?)
 * sont couverts par [TopButtonsTest].
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MenuNavigationTest {

    @Test
    fun t01_homeButtonsNavigateAndSystemBackReturnsHome() {
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
    fun t02_browseChoiceButtonsNavigateToEachList() {
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
    fun t03_quizChoiceButtonsNavigate() {
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

        scenario.close()
    }

    @Test
    fun t04_qcmDomainChoiceHasOnlyTwoDomains() {
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
    fun t05_classicDomainChoiceHasThreeDomainsIncludingPlaces() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // quizChoice
        onView(withId(R.id.btn_primary_2)).perform(click()) // classicDomainChoice
        scenario.assertCurrentDestination(R.id.classicDomainChoiceFragment)

        onView(withId(R.id.btn_primary_1)).perform(click()) // Entités
        scenario.assertCurrentDestination(R.id.quizEntityChoiceFragment)
        pressBack()
        scenario.assertCurrentDestination(R.id.classicDomainChoiceFragment)

        onView(withId(R.id.btn_primary_2)).perform(click()) // Artéfacts
        scenario.assertCurrentDestination(R.id.quizArtifactChoiceFragment)
        pressBack()

        onView(withId(R.id.btn_primary_3)).perform(click()) // Lieux
        scenario.assertCurrentDestination(R.id.quizPlaceChoiceFragment)
        pressBack()

        scenario.close()
    }
}
