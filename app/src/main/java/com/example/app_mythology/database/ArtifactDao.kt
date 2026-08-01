package com.example.app_mythology.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ArtifactDao {

    @Query("SELECT * FROM artifacts ORDER BY name ASC")
    fun getAll(): LiveData<List<ArtifactEntity>>

    /** Chargement ponctuel (non observé) de tous les artéfacts, pour le mode Liste et les leurres du QCM. */
    @Query("SELECT * FROM artifacts")
    suspend fun getAllSync(): List<ArtifactEntity>

    @Query("SELECT * FROM artifacts WHERE id = :id")
    suspend fun getById(id: Int): ArtifactEntity?

    @Query("SELECT * FROM artifacts WHERE mythology = :mythology ORDER BY name ASC")
    fun getByMythology(mythology: String): LiveData<List<ArtifactEntity>>

    @Query("SELECT * FROM artifacts WHERE artifactType = :artifactType ORDER BY name ASC")
    fun getByType(artifactType: String): LiveData<List<ArtifactEntity>>

    @Query("SELECT * FROM artifacts WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): LiveData<List<ArtifactEntity>>

    /** Tire jusqu'à [limit] artéfacts aléatoires d'un niveau de difficulté donné. */
    @Query("SELECT * FROM artifacts WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomByDifficulty(difficulty: Int, limit: Int): List<ArtifactEntity>

    @Query("SELECT DISTINCT mythology FROM artifacts ORDER BY mythology ASC")
    fun getDistinctMythologies(): LiveData<List<String>>

    @Query("SELECT DISTINCT artifactType FROM artifacts ORDER BY artifactType ASC")
    fun getDistinctTypes(): LiveData<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artifact: ArtifactEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(artifacts: List<ArtifactEntity>)

    @Update
    suspend fun update(artifact: ArtifactEntity)

    @Delete
    suspend fun delete(artifact: ArtifactEntity)

    @Query("DELETE FROM artifacts WHERE id = :id")
    suspend fun deleteById(id: Int)
}
