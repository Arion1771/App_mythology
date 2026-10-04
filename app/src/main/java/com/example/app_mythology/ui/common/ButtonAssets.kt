package com.example.app_mythology.ui.common

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageButton

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
