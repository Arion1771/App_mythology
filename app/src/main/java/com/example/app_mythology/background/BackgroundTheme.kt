package com.example.app_mythology.background

import com.example.app_mythology.R

/**
 * Thèmes de fond disponibles pour l'ensemble des écrans de l'application.
 * Un seul thème pour l'instant (voir [BackgroundThemeManager]) ; chaque
 * thème futur n'a besoin que d'un id stable (persisté) et d'un drawable.
 */
enum class BackgroundTheme(val id: String, val displayName: String, val drawableRes: Int) {
    DEFAULT("default", "Sombre (défaut)", R.drawable.bg_theme_default);

    companion object {
        fun fromId(id: String?): BackgroundTheme = entries.firstOrNull { it.id == id } ?: DEFAULT
    }
}
