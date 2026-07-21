package com.example.app_mythology.repository

import androidx.lifecycle.LiveData
import com.example.app_mythology.database.AppDatabase
import com.example.app_mythology.database.EntiteDao
import com.example.app_mythology.database.EntiteEntity

class EntiteRepository(private val dao: EntiteDao) {

    val allEntites: LiveData<List<EntiteEntity>> = dao.getAll()
    val mythologies: LiveData<List<String>> = dao.getDistinctMythologies()
    val races: LiveData<List<String>> = dao.getDistinctRaces()

    suspend fun getById(id: Int): EntiteEntity? = dao.getById(id)

    fun getByMythology(mythology: String) = dao.getByMythology(mythology)
    fun getByRace(race: String) = dao.getByRace(race)
    fun getByMythologyAndRace(mythology: String, race: String) =
        dao.getByMythologyAndRace(mythology, race)
    fun search(query: String) = dao.search(query)

    suspend fun getRandomForQuiz() = dao.getRandomForQuiz()
    suspend fun getRandomForQuizByMythology(mythology: String) =
        dao.getRandomForQuizByMythology(mythology)

    suspend fun insert(entite: EntiteEntity): Long = dao.insert(entite)
    suspend fun insertAll(entites: List<EntiteEntity>) = dao.insertAll(entites)
    suspend fun update(entite: EntiteEntity) = dao.update(entite)
    suspend fun delete(entite: EntiteEntity) = dao.delete(entite)
    suspend fun deleteById(id: Int) = dao.deleteById(id)

    companion object {
        @Volatile private var INSTANCE: EntiteRepository? = null
        fun getInstance(db: AppDatabase) = INSTANCE ?: synchronized(this) {
            EntiteRepository(db.entiteDao()).also { INSTANCE = it }
        }
    }
}
