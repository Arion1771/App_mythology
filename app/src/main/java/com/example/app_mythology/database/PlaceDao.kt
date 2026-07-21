package com.example.app_mythology.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlaceDao {

    @Query("SELECT * FROM places ORDER BY name ASC")
    fun getAll(): LiveData<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getById(id: Int): PlaceEntity?

    @Query("SELECT * FROM places WHERE placeType = 'River' ORDER BY name ASC")
    fun getAllRivers(): LiveData<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE placeType = 'Realm' ORDER BY name ASC")
    fun getAllRealms(): LiveData<List<PlaceEntity>>

    // Quiz "Arbre Monde" : royaumes nordiques de l'Yggdrasil
    @Query("SELECT * FROM places WHERE placeType = 'Realm' AND mythology = 'Nordique' ORDER BY name ASC")
    fun getYggdrasilRealms(): LiveData<List<PlaceEntity>>

    // Quiz "Fleuve de l'Enfer" : fleuves grecs des Enfers
    @Query("SELECT * FROM places WHERE placeType = 'River' AND mythology = 'Grecque' ORDER BY name ASC")
    fun getHellRivers(): LiveData<List<PlaceEntity>>

    // Quiz "Royaume des Morts" : lieux des Enfers grecs
    @Query("SELECT * FROM places WHERE placeType = 'Underworld' ORDER BY name ASC")
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
