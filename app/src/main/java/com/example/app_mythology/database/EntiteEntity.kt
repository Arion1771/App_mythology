package com.example.app_mythology.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entites")
data class EntiteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // ─── Communs ───────────────────────────────────────────────────────────
    val name: String,
    val mythology: String,
    val race: String,

    // ─── Quiz ──────────────────────────────────────────────────────────────
    val clue: String? = null,        // Indice affiché pendant le quiz
    val difficulty: Int = 1,         // 1 = facile, 2 = moyen, 3 = difficile

    // ─── God ───────────────────────────────────────────────────────────────
    val domain: String? = null,
    val godType: String? = null,
    val equivalentName: String? = null,
    val fatherName: String? = null,
    val motherName: String? = null,

    // ─── Giant ─────────────────────────────────────────────────────────────
    val giantType: String? = null,
    val opponentName: String? = null,

    // ─── Heroes ────────────────────────────────────────────────────────────
    val story: String? = null,
    val killer: String? = null,
    val ascendantName: String? = null,

    // ─── Monster ───────────────────────────────────────────────────────────
    val monsterType: String? = null,
    val description: String? = null,

    // ─── Cyclope ───────────────────────────────────────────────────────────
    val primordial: Boolean? = null,

    // ─── Muses ─────────────────────────────────────────────────────────────
    val museType: String? = null,

    // ─── Archangels ────────────────────────────────────────────────────────
    val role: String? = null,

    // ─── Arthurian_Knight ──────────────────────────────────────────────────
    val death: String? = null,

    // ─── Zodiacal_Sign ─────────────────────────────────────────────────────
    val zodiacType: String? = null,
    val chineseEquivalent: String? = null
)
