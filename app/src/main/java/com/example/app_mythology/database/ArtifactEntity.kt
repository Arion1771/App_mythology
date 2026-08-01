package com.example.app_mythology.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * artifactType : "Arme" | "Artefact" | "Objet magique" | "Véhicule" | "Nourriture"
 */
@Entity(tableName = "artifacts")
data class ArtifactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val mythology: String,
    val artifactType: String,

    val ownerName: String? = null,
    val creatorName: String? = null,
    val power: String? = null,
    val story: String? = null,
    val description: String? = null,

    // ─── Quiz ──────────────────────────────────────────────────────────────
    val clue: String? = null,
    val difficulty: Int = 1,

    // ─── Mode QCM ──────────────────────────────────────────────────────────
    val tags: String? = null   // Tags thématiques séparés par virgule (leurres du QCM)
)
