package com.example.app_mythology

import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.app_mythology.ui.MainActivity
import com.example.app_mythology.viewmodel.DuelViewModel
import com.example.app_mythology.viewmodel.QuizViewModel
import org.hamcrest.Matcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert.assertEquals

/**
 * Utilitaires communs aux tests instrumentés (branche Test-Non-Regression) :
 * accès au [NavController] réel de [MainActivity] (le même nav_graph.xml que
 * l'application utilise, pas un graphe de test reconstruit à la main), accès
 * aux ViewModels des écrans pour lire la question courante (questions tirées
 * au hasard), saisie de réponses, et une petite attente active pour tolérer
 * les chargements asynchrones (Room/coroutines) que l'auto-synchronisation
 * d'Espresso ne couvre pas.
 */

/** Réponse qui ne correspond à aucun nom de la base. */
const val WRONG_ANSWER = "__reponse_forcement_fausse__"

/** Les quatre boutons de choix d'une question QCM (quiz et Duel), dans l'ordre des choix. */
val QCM_CHOICE_BUTTONS = listOf(
    R.id.btn_qcm_choice_0, R.id.btn_qcm_choice_1, R.id.btn_qcm_choice_2, R.id.btn_qcm_choice_3
)

private fun navHostOf(activity: MainActivity): NavHostFragment =
    activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

private fun navControllerOf(activity: MainActivity): NavController = navHostOf(activity).navController

fun ActivityScenario<MainActivity>.assertCurrentDestination(expectedId: Int) {
    var actual = 0
    onActivity { activity -> actual = navControllerOf(activity).currentDestination?.id ?: 0 }
    assertEquals(expectedId, actual)
}

/** Attend jusqu'à [timeoutMs] qu'une condition devienne vraie (poll toutes les 100 ms). */
fun waitFor(timeoutMs: Long = 5000, block: () -> Boolean) {
    val deadline = System.currentTimeMillis() + timeoutMs
    while (System.currentTimeMillis() < deadline) {
        if (block()) return
        Thread.sleep(100)
    }
    if (!block()) throw AssertionError("Condition non remplie après ${timeoutMs}ms")
}

/** Attend que la destination courante du NavController atteigne [expectedId]. */
fun ActivityScenario<MainActivity>.waitForDestination(expectedId: Int, timeoutMs: Long = 5000) {
    waitFor(timeoutMs) {
        var actual = 0
        onActivity { activity -> actual = navControllerOf(activity).currentDestination?.id ?: 0 }
        actual == expectedId
    }
}

/** Exécute [block] sur le thread principal et en renvoie le résultat. */
fun <T> ActivityScenario<MainActivity>.onMain(block: (MainActivity) -> T): T {
    var result: T? = null
    onActivity { result = block(it) }
    @Suppress("UNCHECKED_CAST")
    return result as T
}

/** ViewModel partagé par un graphe de navigation (quiz Classique / QCM, Duel). */
inline fun <reified VM : ViewModel> ActivityScenario<MainActivity>.graphViewModel(graphId: Int): VM =
    onMain { activity ->
        val navHost = activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        ViewModelProvider(navHost.navController.getBackStackEntry(graphId))[VM::class.java]
    }

/** ViewModel propre au fragment affiché (quiz Lieux / Liste). */
fun ActivityScenario<MainActivity>.currentFragmentQuizViewModel(): QuizViewModel = onMain {
    val fragment: Fragment = navHostOf(it).childFragmentManager.primaryNavigationFragment!!
    ViewModelProvider(fragment)[QuizViewModel::class.java]
}

/**
 * Saisit [answer] dans [inputId] puis clique sur [validateId]. Saisie directe
 * (accents compris, que typeText ne sait pas taper), clavier fermé et, sur
 * les écrans défilants, défilement jusqu'au bouton : le clavier peut sinon
 * masquer « Valider » (Espresso exige 90 % de la vue visible).
 */
fun answerAndValidate(answer: String, inputId: Int, validateId: Int, scrollable: Boolean) {
    onView(withId(inputId)).perform(replaceText(answer))
    closeSoftKeyboard()
    if (scrollable) onView(withId(validateId)).perform(scrollTo(), click())
    else onView(withId(validateId)).perform(click())
}

/** Clique le premier bouton enfant d'un conteneur créé dynamiquement (thèmes du mode Liste). */
fun clickFirstChildButton(): ViewAction = object : ViewAction {
    override fun getConstraints(): Matcher<android.view.View> = IsInstanceOf.instanceOf(ViewGroup::class.java)
    override fun getDescription() = "clique le premier bouton enfant"
    override fun perform(uiController: UiController, view: android.view.View) {
        val group = view as ViewGroup
        (0 until group.childCount).map { group.getChildAt(it) }.first { it is Button }.performClick()
        uiController.loopMainThreadUntilIdle()
    }
}

/**
 * Mise en place d'un duel à 2 joueurs (noms par défaut, niveau facile) depuis
 * l'accueil jusqu'à la première question ; renvoie le ViewModel du duel.
 */
fun startDuel(
    scenario: ActivityScenario<MainActivity>, modeButton: Int, poolButton: Int, questionDest: Int,
): DuelViewModel {
    onView(withId(R.id.btn_primary_3)).perform(click()) // Home -> Duel (nombre de joueurs)
    scenario.waitForDestination(R.id.duelPlayerCountFragment)
    onView(withId(R.id.btn_duel_players_next)).perform(click()) // 2 joueurs par défaut
    scenario.assertCurrentDestination(R.id.duelPlayerNamesFragment)
    onView(withId(R.id.btn_duel_names_next)).perform(click()) // noms par défaut (Joueur 1/2)
    scenario.assertCurrentDestination(R.id.duelModeChoiceFragment)

    onView(withId(modeButton)).perform(click())
    scenario.assertCurrentDestination(R.id.duelDifficultyChoiceFragment)
    onView(withId(R.id.btn_level_easy)).perform(click())
    scenario.assertCurrentDestination(R.id.duelPoolChoiceFragment)
    onView(withId(poolButton)).perform(click())
    scenario.waitForDestination(R.id.duelAnnounceFragment)
    onView(withId(R.id.tv_duel_announce_player)).check(matches(isDisplayed()))

    onView(withId(R.id.btn_duel_announce_start)).perform(click())
    scenario.waitForDestination(questionDest)
    return scenario.graphViewModel(R.id.duel_graph)
}

/** Score formaté exactement comme l'affichent les écrans de score (entier, sinon une décimale). */
fun formatScoreLikeApp(v: Double): String =
    if (v == Math.round(v).toDouble()) Math.round(v).toString() else String.format("%.1f", v)

