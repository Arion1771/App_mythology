package com.example.app_mythology.background

/**
 * Thèmes de fond disponibles pour l'ensemble des écrans de l'application.
 * Un seul thème pour l'instant ; chaque thème futur n'a besoin que d'un id
 * stable (persisté) et d'un asset image `assets/background/<asset>.png`
 * (voir [BackgroundAssets]), déposé de la même façon que les boutons
 * (`assets/button/`) et les succès (`assets/achievements/`).
 */
enum class BackgroundTheme(val id: String, val displayName: String, val asset: String) {
    DEFAULT("default", "Sombre (défaut)", "default");

    companion object {
        fun fromId(id: String?): BackgroundTheme = entries.firstOrNull { it.id == id } ?: DEFAULT
    }
}
