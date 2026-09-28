package com.example.app_mythology.background

import android.content.Context
import android.content.SharedPreferences

/**
 * Persiste le thème de fond choisi par l'utilisateur, dans des SharedPreferences
 * dédiées (comme AchievementManager). Le thème sélectionné est appliqué une
 * seule fois, au conteneur racine de MainActivity (voir activity_main.xml) :
 * les fragments restent transparents, ce qui permet de changer de thème sans
 * toucher chaque écran individuellement quand un sélecteur sera ajouté.
 */
object BackgroundThemeManager {

    private const val PREFS_NAME = "background_theme"
    private const val KEY_SELECTED = "selected_theme_id"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getSelected(): BackgroundTheme = BackgroundTheme.fromId(prefs.getString(KEY_SELECTED, null))

    fun setSelected(theme: BackgroundTheme) {
        prefs.edit().putString(KEY_SELECTED, theme.id).apply()
    }
}
