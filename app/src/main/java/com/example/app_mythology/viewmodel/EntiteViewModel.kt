package com.example.app_mythology.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.app_mythology.database.AppDatabase
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.repository.EntiteRepository
import kotlinx.coroutines.launch

class EntiteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EntiteRepository.getInstance(
        AppDatabase.getInstance(application)
    )

    private val _filterMode = MutableLiveData<FilterMode>(FilterMode.ALL)
    private val _filterValue = MutableLiveData<String>("")

    enum class FilterMode { ALL, BY_RACE, BY_MYTHOLOGY, BY_MYTHOLOGY_AND_RACE, SEARCH }

    // MediatorLiveData corrigé — évite le crash dû aux sources multiples simultanées
    private var currentSource: LiveData<List<EntiteEntity>>? = null
    val entites: MediatorLiveData<List<EntiteEntity>> = MediatorLiveData<List<EntiteEntity>>().apply {
        fun refresh() {
            currentSource?.let { removeSource(it) }
            val mode = _filterMode.value ?: FilterMode.ALL
            val value = _filterValue.value ?: ""
            val newSource: LiveData<List<EntiteEntity>> = when (mode) {
                FilterMode.ALL -> repository.allEntites
                FilterMode.BY_RACE -> repository.getByRace(value)
                FilterMode.BY_MYTHOLOGY -> repository.getByMythology(value)
                FilterMode.BY_MYTHOLOGY_AND_RACE -> {
                    val parts = value.split("|")
                    repository.getByMythologyAndRace(parts[0], parts.getOrElse(1) { "" })
                }
                FilterMode.SEARCH -> repository.search(value)
            }
            currentSource = newSource
            addSource(newSource) { setValue(it) }
        }
        addSource(_filterMode) { refresh() }
        addSource(_filterValue) { refresh() }
    }

    val mythologies: LiveData<List<String>> = repository.mythologies
    val races: LiveData<List<String>> = repository.races

    fun showAll() { _filterMode.value = FilterMode.ALL; _filterValue.value = "" }
    fun filterByRace(race: String) { _filterMode.value = FilterMode.BY_RACE; _filterValue.value = race }
    fun filterByMythology(mythology: String) { _filterMode.value = FilterMode.BY_MYTHOLOGY; _filterValue.value = mythology }
    fun filterByMythologyAndRace(mythology: String, race: String) {
        _filterMode.value = FilterMode.BY_MYTHOLOGY_AND_RACE; _filterValue.value = "$mythology|$race"
    }
    fun search(query: String) { _filterMode.value = FilterMode.SEARCH; _filterValue.value = query }

    fun insert(entite: EntiteEntity) = viewModelScope.launch { repository.insert(entite) }
    fun update(entite: EntiteEntity) = viewModelScope.launch { repository.update(entite) }
    fun delete(entite: EntiteEntity) = viewModelScope.launch { repository.delete(entite) }
}
