package com.example.app_mythology.navigation

import android.graphics.Rect
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.R
import com.example.app_mythology.assertCurrentDestination
import com.example.app_mythology.onMain
import com.example.app_mythology.ui.MainActivity
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import kotlin.math.abs

/** Rectangle à l'écran de la vue [id] de l'activité. */
private fun ActivityScenario<MainActivity>.screenRect(id: Int): Rect = onMain { activity ->
    val view = activity.findViewById<View>(id)
    val loc = IntArray(2).also { view.getLocationOnScreen(it) }
    Rect(loc[0], loc[1], loc[0] + view.width, loc[1] + view.height)
}

/**
 * Présence et fonctionnement des boutons d'angle : retour (←, tous les
 * écrans sauf l'accueil), Succès (🏆, accueil) et Aide (?, choix du quiz),
 * ainsi que la version affichée sur l'accueil — branche Test-Non-Regression.
 * Le bouton retour avait disparu avec la barre d'action en V4.3.1.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class TopButtonsTest {

    @Test
    fun t01_homeShowsTrophyAndVersionButNoBackButton() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_trophy)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_version)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_back)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        scenario.close()
    }

    @Test
    fun t02_trophyOpensAchievementsAndBackButtonReturnsHome() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_trophy)).perform(click())
        scenario.assertCurrentDestination(R.id.achievementsFragment)
        onView(withId(R.id.btn_back)).check(matches(isDisplayed()))

        onView(withId(R.id.btn_back)).perform(click())
        scenario.assertCurrentDestination(R.id.homeFragment)
        onView(withId(R.id.btn_back)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        scenario.close()
    }

    @Test
    fun t03_quizChoiceShowsHelpAndBackButtons() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        scenario.assertCurrentDestination(R.id.quizChoiceFragment)
        onView(withId(R.id.btn_quiz_help)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_back)).check(matches(isDisplayed()))

        // Deux niveaux à remonter avec le bouton retour : aide -> choix du quiz -> accueil.
        onView(withId(R.id.btn_quiz_help)).perform(click())
        scenario.assertCurrentDestination(R.id.quizHelpFragment)
        onView(withId(R.id.btn_back)).perform(click())
        scenario.assertCurrentDestination(R.id.quizChoiceFragment)
        onView(withId(R.id.btn_back)).perform(click())
        scenario.assertCurrentDestination(R.id.homeFragment)
        scenario.close()
    }

    /** Champ de recherche sur la ligne du bouton retour, démarrant après lui (donc sans recouvrement). */
    @Test
    fun t04_listSearchFieldSharesTheBackButtonLine() {
        for (button in listOf(R.id.btn_primary_1, R.id.btn_primary_2, R.id.btn_primary_3)) {
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            onView(withId(R.id.btn_primary_1)).perform(click()) // Données
            onView(withId(button)).perform(click())             // Entités / Lieux / Artéfacts
            onView(withId(R.id.search_view)).check(matches(isDisplayed()))

            val back = scenario.screenRect(R.id.btn_back)
            val search = scenario.screenRect(R.id.search_view)
            assertTrue(
                "Recherche et bouton retour doivent être sur la même ligne ($back / $search)",
                abs(back.centerY() - search.centerY()) <= 2
            )
            assertTrue(
                "La recherche doit commencer après le bouton retour ($back / $search)",
                search.left >= back.right
            )
            scenario.close()
        }
    }
}
