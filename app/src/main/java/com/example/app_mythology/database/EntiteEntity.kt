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

    // ─── God ───────────────────────────────────────────────────────────────
    val domain: String? = null,
    val godType: String? = null,
    val equivalentName: String? = null,
    val fatherName: String? = null,
    val motherName: String? = null,

    // ─── Titan ─────────────────────────────────────────────────────────────
    // domain partagé

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
)
