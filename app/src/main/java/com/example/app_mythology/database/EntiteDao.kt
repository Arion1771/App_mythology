package com.example.app_mythology.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EntiteDao {

    @Query("SELECT COUNT(*) FROM entites")
    suspend fun countAll(): Int

    @Query("SELECT * FROM entites ORDER BY name ASC")
    fun getAll(): LiveData<List<EntiteEntity>>

    @Query("SELECT * FROM entites WHERE id = :id")
    suspend fun getById(id: Int): EntiteEntity?

    @Query("SELECT * FROM entites WHERE mythology = :mythology ORDER BY name ASC")
    fun getByMythology(mythology: String): LiveData<List<EntiteEntity>>

    @Query("SELECT * FROM entites WHERE race = :race ORDER BY name ASC")
    fun getByRace(race: String): LiveData<List<EntiteEntity>>

    @Query("SELECT * FROM entites WHERE mythology = :mythology AND race = :race ORDER BY name ASC")
    fun getByMythologyAndRace(mythology: String, race: String): LiveData<List<EntiteEntity>>

    @Query("SELECT * FROM entites WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): LiveData<List<EntiteEntity>>

    @Query("SELECT * FROM entites ORDER BY RANDOM() LIMIT 10")
    suspend fun getRandomForQuiz(): List<EntiteEntity>

    /** Tire jusqu'à [limit] entités aléatoires d'un niveau de difficulté donné. */
    @Query("SELECT * FROM entites WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomByDifficulty(difficulty: Int, limit: Int): List<EntiteEntity>

    @Query("SELECT DISTINCT mythology FROM entites ORDER BY mythology ASC")
    fun getDistinctMythologies(): LiveData<List<String>>

    @Query("SELECT DISTINCT race FROM entites ORDER BY race ASC")
    fun getDistinctRaces(): LiveData<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entite: EntiteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entites: List<EntiteEntity>)

    @Update
    suspend fun update(entite: EntiteEntity)

    @Delete
    suspend fun delete(entite: EntiteEntity)

    @Query("DELETE FROM entites WHERE id = :id")
    suspend fun deleteById(id: Int)
}
