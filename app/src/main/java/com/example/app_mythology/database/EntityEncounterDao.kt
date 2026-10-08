package com.example.app_mythology.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EntityEncounterDao {

    @Insert
    suspend fun insert(encounter: EntityEncounterEntity)

    @Query("SELECT COUNT(*) FROM entity_encounters WHERE entityKey = :key")
    suspend fun countForKey(key: String): Int

    /** Comptes groupés par clé, pour vérifier en une seule requête si tout un palier a atteint le seuil. */
    @Query("SELECT entityKey, COUNT(*) as cnt FROM entity_encounters WHERE entityKey IN (:keys) GROUP BY entityKey")
    suspend fun countForKeys(keys: List<String>): List<EntityKeyCount>

    /** Les [limit] dernières rencontres d'une entité (plus récente en premier), pour calculer son niveau interne. */
    @Query("SELECT faute FROM entity_encounters WHERE entityKey = :key ORDER BY id DESC LIMIT :limit")
    suspend fun lastFautesForKey(key: String, limit: Int): List<Double>

    @Query("DELETE FROM entity_encounters WHERE entityKey = :key")
    suspend fun deleteForKey(key: String)

    @Query("DELETE FROM entity_encounters WHERE entityKey IN (:keys)")
    suspend fun deleteForKeys(keys: List<String>)
}

data class EntityKeyCount(val entityKey: String, val cnt: Int)
