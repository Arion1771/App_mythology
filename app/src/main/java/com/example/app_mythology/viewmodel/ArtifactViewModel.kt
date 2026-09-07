package com.example.app_mythology.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.app_mythology.database.AppDatabase
import com.example.app_mythology.database.ArtifactEntity
import com.example.app_mythology.repository.ArtifactRepository
import com.example.app_mythology.repository.EntiteRepository

class ArtifactViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ArtifactRepository.getInstance(
        AppDatabase.getInstance(application)
    )

    // Sert uniquement à récupérer l'ordre canonique des mythologies (par nombre d'entités
    // décroissant), pour trier la liste des mythologies ayant des artéfacts de la même façon.
    private val entiteRepository = EntiteRepository.getInstance(AppDatabase.getInstance(application))

    private val _filterMode = MutableLiveData<FilterMode>(FilterMode.ALL)
    private val _filterValue = MutableLiveData<String>("")

    enum class FilterMode { ALL, BY_TYPE, BY_MYTHOLOGY, SEARCH }

    private var currentSource: LiveData<List<ArtifactEntity>>? = null
    val artifacts: MediatorLiveData<List<ArtifactEntity>> = MediatorLiveData<List<ArtifactEntity>>().apply {
        fun refresh() {
            currentSource?.let { removeSource(it) }
            val mode = _filterMode.value ?: FilterMode.ALL
            val value = _filterValue.value ?: ""
            val newSource: LiveData<List<ArtifactEntity>> = when (mode) {
                FilterMode.ALL -> repository.allArtifacts
                FilterMode.BY_TYPE -> repository.getByType(value)
                FilterMode.BY_MYTHOLOGY -> repository.getByMythology(value)
                FilterMode.SEARCH -> repository.search(value)
            }
            currentSource = newSource
            addSource(newSource) { setValue(it) }
        }
        addSource(_filterMode) { refresh() }
        addSource(_filterValue) { refresh() }
    }

    // Mythologies ayant des artéfacts, triées selon l'ordre canonique (nombre d'entités
    // décroissant) fourni par EntiteRepository plutôt que par ordre alphabétique.
    val mythologies: MediatorLiveData<List<String>> = MediatorLiveData<List<String>>().apply {
        var ranked: List<String> = emptyList()
        var raw: List<String> = emptyList()
        fun refresh() {
            val rankIndex = ranked.withIndex().associate { (i, m) -> m to i }
            value = raw.sortedWith(compareBy({ rankIndex[it] ?: Int.MAX_VALUE }, { it }))
        }
        addSource(entiteRepository.mythologies) { ranked = it; refresh() }
        addSource(repository.mythologies) { raw = it; refresh() }
    }
    val types: LiveData<List<String>> = repository.types

    fun showAll() { _filterMode.value = FilterMode.ALL; _filterValue.value = "" }
    fun filterByType(type: String) { _filterMode.value = FilterMode.BY_TYPE; _filterValue.value = type }
    fun filterByMythology(mythology: String) { _filterMode.value = FilterMode.BY_MYTHOLOGY; _filterValue.value = mythology }
    fun search(query: String) { _filterMode.value = FilterMode.SEARCH; _filterValue.value = query }
}
