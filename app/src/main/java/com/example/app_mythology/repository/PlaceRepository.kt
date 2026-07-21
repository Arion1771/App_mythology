package com.example.app_mythology.repository

import androidx.lifecycle.LiveData
import com.example.app_mythology.database.AppDatabase
import com.example.app_mythology.database.PlaceDao
import com.example.app_mythology.database.PlaceEntity

class PlaceRepository(private val dao: PlaceDao) {

    val allPlaces: LiveData<List<PlaceEntity>> = dao.getAll()
    val allRivers: LiveData<List<PlaceEntity>> = dao.getAllRivers()
    val allRealms: LiveData<List<PlaceEntity>> = dao.getAllRealms()
    val yggdrasilRealms: LiveData<List<PlaceEntity>> = dao.getYggdrasilRealms()
    val hellRivers: LiveData<List<PlaceEntity>> = dao.getHellRivers()
    val underworldPlaces: LiveData<List<PlaceEntity>> = dao.getUnderworldPlaces()

    suspend fun getById(id: Int): PlaceEntity? = dao.getById(id)
    fun getByMythology(mythology: String) = dao.getByMythology(mythology)
    fun search(query: String) = dao.search(query)

    suspend fun insert(place: PlaceEntity): Long = dao.insert(place)
    suspend fun insertAll(places: List<PlaceEntity>) = dao.insertAll(places)
    suspend fun update(place: PlaceEntity) = dao.update(place)
    suspend fun delete(place: PlaceEntity) = dao.delete(place)
    suspend fun deleteById(id: Int) = dao.deleteById(id)

    companion object {
        @Volatile private var INSTANCE: PlaceRepository? = null
        fun getInstance(db: AppDatabase) = INSTANCE ?: synchronized(this) {
            PlaceRepository(db.placeDao()).also { INSTANCE = it }
        }
    }
}
