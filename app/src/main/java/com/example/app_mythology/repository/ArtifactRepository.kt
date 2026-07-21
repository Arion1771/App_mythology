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

    fun getByMythology(mythology: String) = dao.getByMythology(mythology)
    fun getByType(artifactType: String) = dao.getByType(artifactType)
    fun search(query: String) = dao.search(query)

    suspend fun getRandomByDifficulty(difficulty: Int, limit: Int) =
        dao.getRandomByDifficulty(difficulty, limit)

    suspend fun insert(artifact: ArtifactEntity): Long = dao.insert(artifact)
    suspend fun insertAll(artifacts: List<ArtifactEntity>) = dao.insertAll(artifacts)
    suspend fun update(artifact: ArtifactEntity) = dao.update(artifact)
    suspend fun delete(artifact: ArtifactEntity) = dao.delete(artifact)
    suspend fun deleteById(id: Int) = dao.deleteById(id)

    companion object {
        @Volatile private var INSTANCE: ArtifactRepository? = null
        fun getInstance(db: AppDatabase): ArtifactRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ArtifactRepository(db.artifactDao()).also { INSTANCE = it }
            }
        }
    }
}
