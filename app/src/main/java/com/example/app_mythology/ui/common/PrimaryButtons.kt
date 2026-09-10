package com.example.app_mythology.ui.common

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import com.example.app_mythology.R

/**
 * Un bouton principal « image » : [asset] est le nom de fichier (sans
 * extension) dans `assets/button/`, [contentDescription] le libellé
 * d'accessibilité (le texte visible est incrusté dans l'image).
 */
data class PrimaryButton(
    val asset: String,
    val contentDescription: String,
    val onClick: () -> Unit,
)

/**
 * Renseigne les trois emplacements de `view_primary_buttons` à partir des
 * images de `assets/button/<asset>.png`. Les emplacements non fournis sont
 * masqués (GONE) — la colonne reste centrée, donc les boutons présents ne
 * bougent pas d'un pixel d'un écran à l'autre.
 */
fun View.bindPrimaryButtons(buttons: List<PrimaryButton>) {
    val slots = intArrayOf(R.id.btn_primary_1, R.id.btn_primary_2, R.id.btn_primary_3)
    for (i in slots.indices) {
        val slot = findViewById<ImageButton>(slots[i])
        val spec = buttons.getOrNull(i)
        if (spec == null) {
            slot.visibility = View.GONE
            continue
        }
        slot.visibility = View.VISIBLE
        slot.setImageDrawable(loadButtonAsset(spec.asset))
        slot.contentDescription = spec.contentDescription
        slot.setOnClickListener { spec.onClick() }
    }
}

private fun View.loadButtonAsset(name: String): Drawable? =
    try {
        context.assets.open("button/$name.png").use { Drawable.createFromStream(it, name) }
    } catch (e: Exception) {
        null
    }
