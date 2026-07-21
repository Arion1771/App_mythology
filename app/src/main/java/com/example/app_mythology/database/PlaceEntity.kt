package com.example.app_mythology.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * placeType : "River" | "Realm" | "Underworld"
 * Pour Underworld, le champ [region] précise : Elysée, Tartare,
 * Champs d'Asphodèle, Champs du Châtiment, Érèbe, etc.
 */
@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // ─── Communs (Place) ───────────────────────────────────────────────────
    val name: String,
    val mythology: String,
    val description: String,
    val placeType: String,

    // ─── River ─────────────────────────────────────────────────────────────
    val particularity: String? = null,

    // ─── Realm (Yggdrasil + autres royaumes) ──────────────────────────────
    val inhabitants: String? = null,

    // ─── Underworld (Enfers grecs) ─────────────────────────────────────────
    val region: String? = null
)
