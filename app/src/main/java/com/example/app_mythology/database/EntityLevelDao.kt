package com.example.app_mythology.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EntityLevelDao {

    @Query("SELECT * FROM entity_levels WHERE entityKey = :key")
    suspend fun getByKey(key: String): EntityLevelEntity?

    @Query("SELECT * FROM entity_levels WHERE entityKey IN (:keys)")
    suspend fun getByKeys(keys: List<String>): List<EntityLevelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(levels: List<EntityLevelEntity>)

    @Query("DELETE FROM entity_levels WHERE entityKey IN (:keys)")
    suspend fun deleteForKeys(keys: List<String>)
}
