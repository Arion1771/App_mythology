package com.example.app_mythology.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Une rencontre avec une entité dans le quiz solo (Classique ou QCM), utilisée
 * pour calculer le niveau interne de difficulté adaptative de l'entité (voir
 * [EntityLevelEntity]). [entityKey] est la clé stable nom+mythologie+race
 * (calculée par EntityDifficultyEngine.keyOf), qui survit aux rechargements
 * de prepopulate.json même si l'id Room de l'entité change.
 */
@Entity(tableName = "entity_encounters")
data class EntityEncounterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val entityKey: String,
    val faute: Double   // 0.0 (juste du 1er coup), 0.5 (juste au 2e essai), 1.0 (jamais juste)
)
