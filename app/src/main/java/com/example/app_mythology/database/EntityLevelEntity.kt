package com.example.app_mythology.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Niveau interne de difficulté adaptative d'une entité (1 à 3), fixé
 * définitivement une fois calculé (voir EntityDifficultyEngine) et conservé
 * entre les rechargements de prepopulate.json tant que [contentHash] ne
 * change pas (càd tant que les champs utilisés dans les questions — nom,
 * mythologie, race, domaine, indice, difficulté canonique — restent
 * identiques pour cette entité).
 */
@Entity(tableName = "entity_levels")
data class EntityLevelEntity(
    @PrimaryKey
    val entityKey: String,

    val level: Int,          // 1 à 3, fixé définitivement au moment du calcul
    val contentHash: String  // détecte un changement de l'entité source → reset
)
