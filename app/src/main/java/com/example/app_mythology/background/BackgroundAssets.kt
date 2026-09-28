package com.example.app_mythology.background

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.app_mythology.R

/**
 * Charge le fond depuis `assets/background/<asset>.png`, comme
 * PrimaryButtons.kt charge les boutons depuis `assets/button/` et
 * AchievementsFragment charge les succès depuis `assets/achievements/`.
 * Tant que le fichier n'est pas déposé, se rabat sur bg_theme_default (halo
 * discret proche de l'ancien fond uni), à la manière du blason générique de
 * secours des succès.
 */
object BackgroundAssets {

    fun load(context: Context, theme: BackgroundTheme): Drawable? {
        val fromAsset = try {
            context.assets.open("background/${theme.asset}.png").use {
                Drawable.createFromStream(it, theme.asset)
            }
        } catch (e: Exception) {
            null
        }
        return fromAsset ?: ContextCompat.getDrawable(context, R.drawable.bg_theme_default)
    }
}
