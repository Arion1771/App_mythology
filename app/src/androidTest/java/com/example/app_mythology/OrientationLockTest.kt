package com.example.app_mythology

import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.ui.MainActivity
import org.junit.Assert.assertEquals
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Verrouillage de l'application en portrait (V2.5.0) — branche
 * Test-Non-Regression. Une ancienne version remettait les quiz à zéro lors
 * d'une rotation d'écran ; le verrou empêche toute rotation, il suffit donc
 * de vérifier qu'il est bien en place.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class OrientationLockTest {

    @Test
    fun t01_mainActivityIsDeclaredPortrait() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val info = context.packageManager.getActivityInfo(ComponentName(context, MainActivity::class.java), 0)
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, info.screenOrientation)
    }

    @Test
    fun t02_runningActivityStaysInPortrait() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, scenario.onMain { it.requestedOrientation })
        assertEquals(Configuration.ORIENTATION_PORTRAIT, scenario.onMain { it.resources.configuration.orientation })
        scenario.close()
    }
}
