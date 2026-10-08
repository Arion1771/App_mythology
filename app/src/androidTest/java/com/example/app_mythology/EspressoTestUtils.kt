package com.example.app_mythology

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import com.example.app_mythology.ui.MainActivity
import org.junit.Assert.assertEquals

/**
 * Utilitaires communs aux tests instrumentés (branche Test-Non-Regression) :
 * accès au [NavController] réel de [MainActivity] (le même nav_graph.xml que
 * l'application utilise, pas un graphe de test reconstruit à la main), et
 * une petite attente active pour tolérer les chargements asynchrones (Room/
 * coroutines) que l'auto-synchronisation d'Espresso ne couvre pas.
 */

private fun navControllerOf(activity: MainActivity): NavController {
    val navHost = activity.supportFragmentManager
        .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    return navHost.navController
}

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
