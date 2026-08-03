package com.example.app_mythology.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlaceDao {

    @Query("SELECT * FROM places ORDER BY name ASC")
    fun getAll(): LiveData<List<PlaceEntity>>

    /** Chargement ponctuel (non observé), utilisé pour la resynchronisation depuis prepopulate.json. */
    @Query("SELECT * FROM places")
    suspend fun getAllSync(): List<PlaceEntity>

    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getById(id: Int): PlaceEntity?

    @Query("SELECT * FROM places WHERE placeType = 'Fleuve' ORDER BY name ASC")
    fun getAllRivers(): LiveData<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE placeType = 'Royaume' ORDER BY name ASC")
    fun getAllRealms(): LiveData<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE placeType = 'Royaume' AND mythology = 'Nordique' ORDER BY name ASC")
    fun getYggdrasilRealms(): LiveData<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE placeType = 'Fleuve' AND mythology = 'Grecque' ORDER BY name ASC")
    fun getHellRivers(): LiveData<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE placeType = 'Enfers' ORDER BY name ASC")
    fun getUnderworldPlaces(): LiveData<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE mythology = :mythology ORDER BY name ASC")
    fun getByMythology(mythology: String): LiveData<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): LiveData<List<PlaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(place: PlaceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<PlaceEntity>)

    @Update
    suspend fun update(place: PlaceEntity)

    @Delete
    suspend fun delete(place: PlaceEntity)

    @Query("DELETE FROM places WHERE id = :id")
    suspend fun deleteById(id: Int)
}
