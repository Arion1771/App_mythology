package com.example.app_mythology.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.app_mythology.R
import com.example.app_mythology.achievements.Achievement
import com.example.app_mythology.achievements.AchievementManager
import com.example.app_mythology.background.BackgroundAssets
import com.example.app_mythology.background.BackgroundThemeManager
import java.util.LinkedList

class MainActivity : AppCompatActivity() {

    private val bannerQueue = LinkedList<Achievement>()
    private var bannerShowing = false
    private val bannerHandler = Handler(Looper.getMainLooper())

    /**
     * Écrans qui gèrent eux-mêmes le coin supérieur gauche, sans décalage du
     * contenu : menus à trois boutons principaux centrés (coin libre) et
     * listes de consultation (champ de recherche aligné à droite du bouton).
     */
    private val selfInsetDestinations = setOf(
        R.id.browseChoiceFragment,
        R.id.quizChoiceFragment,
        R.id.qcmDomainChoiceFragment,
        R.id.classicDomainChoiceFragment,
        R.id.quizPlaceChoiceFragment,
        R.id.duelModeChoiceFragment,
        R.id.entityListFragment,
        R.id.artifactListFragment,
        R.id.placeListFragment,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Fond appliqué une seule fois ici pour tous les écrans (les fragments
        // restent transparents) : seul point à mettre à jour pour un futur
        // sélecteur de thème.
        findViewById<View>(R.id.main_root).background =
            BackgroundAssets.load(this, BackgroundThemeManager.getSelected())

        // Bouton retour (plus d'ActionBar depuis V4.3.1) : passe par le
        // dispatcher système pour respecter les retours personnalisés des
        // fragments, et reste masqué sur l'accueil. Sur les menus à trois
        // boutons principaux il se superpose au coin vide (les boutons restent
        // au même pixel que sur l'accueil), sur les listes il partage la ligne
        // du champ de recherche ; ailleurs, le contenu est décalé sous le
        // bouton pour ne pas masquer les titres.
        val backButton = findViewById<View>(R.id.btn_back)
        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        val navHostView = findViewById<View>(R.id.nav_host_fragment)
        val backInset = (72 * resources.displayMetrics.density).toInt()
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHost.navController.addOnDestinationChangedListener { _, destination, _ ->
            val isHome = destination.id == R.id.homeFragment
            backButton.visibility = if (isHome) View.GONE else View.VISIBLE
            val inset = if (isHome || destination.id in selfInsetDestinations) 0 else backInset
            navHostView.setPadding(0, inset, 0, 0)
        }

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
}
