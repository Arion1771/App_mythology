package com.example.app_mythology.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.app_mythology.R
import com.example.app_mythology.achievements.Achievement
import com.example.app_mythology.achievements.AchievementManager
import java.util.LinkedList

class MainActivity : AppCompatActivity() {

    private val bannerQueue = LinkedList<Achievement>()
    private var bannerShowing = false
    private val bannerHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController
        val appBarConfig = AppBarConfiguration(setOf(R.id.homeFragment))

        // setupActionBarWithNavController nécessite une ActionBar native,
        // pas une Toolbar manuelle. On utilise NavigationUI directement.
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig)

        AchievementManager.bannerListener = { achievement -> enqueueBanner(achievement) }
    }

    override fun onDestroy() {
        AchievementManager.bannerListener = null
        bannerHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    /** File d'attente : si plusieurs succès se débloquent d'un coup, les bandeaux s'enchaînent. */
    private fun enqueueBanner(achievement: Achievement) {
        bannerQueue.add(achievement)
        if (!bannerShowing) showNextBanner()
    }

    private fun showNextBanner() {
        val next = bannerQueue.poll()
        if (next == null) {
            bannerShowing = false
            return
        }
        bannerShowing = true
        val banner = findViewById<View>(R.id.layout_achievement_banner)
        banner.findViewById<TextView>(R.id.tv_achievement_banner_text).text =
            "Succès débloqué : ${next.name}"
        banner.visibility = View.VISIBLE
        bannerHandler.postDelayed({
            banner.visibility = View.GONE
            showNextBanner()
        }, 3000)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHost.navController.navigateUp() || super.onSupportNavigateUp()
    }
}
