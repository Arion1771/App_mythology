package com.example.app_mythology

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
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
 * Déroulé du mode Duel jusqu'à la première question (Classique et QCM) :
 * nombre de joueurs, noms, type, difficulté, pool de questions, annonce de
 * tour, première question — branche Test-Non-Regression. Une partie complète
 * nécessiterait de répondre à toutes les questions de chaque joueur ; ce
 * test couvre la mise en place et le premier tour, déterministes.
 */
@RunWith(AndroidJUnit4::class)
class DuelFlowTest {

    private fun setUpTwoPlayers(scenario: ActivityScenario<MainActivity>) {
        onView(withId(R.id.btn_primary_3)).perform(click()) // Home -> Duel (nombre de joueurs)
        scenario.waitForDestination(R.id.duelPlayerCountFragment)

        onView(withId(R.id.btn_duel_players_next)).perform(click()) // 2 joueurs par défaut
        scenario.assertCurrentDestination(R.id.duelPlayerNamesFragment)

        onView(withId(R.id.btn_duel_names_next)).perform(click()) // noms par défaut (Joueur 1/2)
        scenario.assertCurrentDestination(R.id.duelModeChoiceFragment)
    }

    @Test
    fun classicDuelReachesFirstQuestion() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        setUpTwoPlayers(scenario)

        onView(withId(R.id.btn_primary_1)).perform(click()) // Classique
        scenario.assertCurrentDestination(R.id.duelDifficultyChoiceFragment)

        onView(withId(R.id.btn_level_easy)).perform(click())
        scenario.assertCurrentDestination(R.id.duelPoolChoiceFragment)

        onView(withId(R.id.btn_duel_pool_same)).perform(click())
        scenario.waitForDestination(R.id.duelAnnounceFragment)
        onView(withId(R.id.tv_duel_announce_player)).check(matches(isDisplayed()))

        onView(withId(R.id.btn_duel_announce_start)).perform(click())
        scenario.waitForDestination(R.id.duelQuestionClassicFragment)
        onView(withId(R.id.et_answer)).check(matches(isDisplayed()))

        // Clavier fermé et défilement jusqu'au bouton avant chaque clic : le
        // clavier ouvert par la saisie peut masquer « Valider » (Espresso
        // exige 90 % de la vue visible).
        onView(withId(R.id.et_answer)).perform(typeText("__reponse_forcement_fausse__"))
        closeSoftKeyboard()
        onView(withId(R.id.btn_validate)).perform(scrollTo(), click()) // essai 1 faux -> essai 2
        // Le passage à l'essai 2 vide le champ, et une saisie vide est ignorée :
        // il faut ressaisir une réponse fausse.
        onView(withId(R.id.et_answer)).perform(scrollTo(), typeText("__reponse_forcement_fausse__"))
        closeSoftKeyboard()
        onView(withId(R.id.btn_validate)).perform(scrollTo(), click()) // essai 2 faux -> révélation -> recap
        scenario.waitForDestination(R.id.duelRecapFragment)
        onView(withId(R.id.tv_result_status)).check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun qcmDuelReachesFirstQuestionAndAnswering() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        setUpTwoPlayers(scenario)

        onView(withId(R.id.btn_primary_2)).perform(click()) // QCM
        scenario.assertCurrentDestination(R.id.duelDifficultyChoiceFragment)

        onView(withId(R.id.btn_level_easy)).perform(click())
        scenario.assertCurrentDestination(R.id.duelPoolChoiceFragment)

        onView(withId(R.id.btn_duel_pool_different)).perform(click())
        scenario.waitForDestination(R.id.duelAnnounceFragment)

        onView(withId(R.id.btn_duel_announce_start)).perform(click())
        scenario.waitForDestination(R.id.duelQuestionQcmFragment)
        onView(withId(R.id.btn_qcm_choice_0)).check(matches(isDisplayed()))

        onView(withId(R.id.btn_qcm_choice_0)).perform(click())
        scenario.waitForDestination(R.id.duelRecapFragment)
        onView(withId(R.id.tv_result_status)).check(matches(isDisplayed()))

        scenario.close()
    }
}
