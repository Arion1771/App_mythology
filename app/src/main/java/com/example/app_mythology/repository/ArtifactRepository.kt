package com.example.app_mythology.repository

import androidx.lifecycle.LiveData
import com.example.app_mythology.database.AppDatabase
import com.example.app_mythology.database.ArtifactDao
import com.example.app_mythology.database.ArtifactEntity

class ArtifactRepository(private val dao: ArtifactDao) {

    val allArtifacts: LiveData<List<ArtifactEntity>> = dao.getAll()
    val mythologies: LiveData<List<String>> = dao.getDistinctMythologies()
    val types: LiveData<List<String>> = dao.getDistinctTypes()

    suspend fun getById(id: Int): ArtifactEntity? = dao.getById(id)
    suspend fun getAllSync(): List<ArtifactEntity> = dao.getAllSync()

    fun getByMythology(mythology: String) = dao.getByMythology(mythology)
    fun getByType(artifactType: String) = dao.getByType(artifactType)
    fun search(query: String) = dao.search(query)

    suspend fun getRandomByDifficulty(difficulty: Int, limit: Int) =
        dao.getRandomByDifficulty(difficulty, limit)

    companion object {
        @Volatile private var INSTANCE: ArtifactRepository? = null
        fun getInstance(db: AppDatabase): ArtifactRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ArtifactRepository(db.artifactDao()).also { INSTANCE = it }
            }
        }
    }
}
