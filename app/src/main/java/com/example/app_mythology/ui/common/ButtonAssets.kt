package com.example.app_mythology.ui.common

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.example.app_mythology.R

/**
 * Charge une image de bouton depuis `assets/button/<name>.png` (plaques du
 * menu principal, mais aussi tout bouton standalone qui réutilise ce même
 * système). Partagé par [bindPrimaryButtons] et par les boutons-images isolés
 * (ex. Valider, Suivant, Retour…).
 */
object ButtonAssets {
    fun load(context: Context, name: String): Drawable? =
        try {
            context.assets.open("button/$name.png").use { Drawable.createFromStream(it, name) }
        } catch (e: Exception) {
            null
        }
}

/** Renseigne un ImageButton standalone depuis `assets/button/<asset>.png`. */
fun ImageButton.setButtonAsset(asset: String, contentDescription: String) {
    setImageDrawable(ButtonAssets.load(context, asset))
    this.contentDescription = contentDescription
}

/**
 * Pose la plaque vide (`assets/button/vide.png`) en fond et la teinte de
 * texte assortie (`@color/on_wood`) sur un bouton dont le contenu est rempli
 * dynamiquement au runtime (choix QCM, thèmes du quiz Liste, bouton
 * « suivant » du récap duel) : son texte ne peut pas être figé dans l'image.
 */
fun Button.setWoodBackground() {
    background = ButtonAssets.load(context, "vide")
    setTextColor(ContextCompat.getColor(context, R.color.on_wood))
}
