package com.example.app_mythology.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.app_mythology.database.AppDatabase
import com.example.app_mythology.database.PlaceEntity
import com.example.app_mythology.repository.PlaceRepository
import kotlinx.coroutines.launch

class PlaceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PlaceRepository.getInstance(AppDatabase.getInstance(application))

    // Correction : observer directement le DAO via LiveData, pas observeForever
    val allPlaces: LiveData<List<PlaceEntity>> = repository.allPlaces
    val allRivers: LiveData<List<PlaceEntity>> = repository.allRivers
    val allRealms: LiveData<List<PlaceEntity>> = repository.allRealms
    val yggdrasilRealms: LiveData<List<PlaceEntity>> = repository.yggdrasilRealms
    val hellRivers: LiveData<List<PlaceEntity>> = repository.hellRivers
    val underworldPlaces: LiveData<List<PlaceEntity>> = repository.underworldPlaces

    fun search(query: String): LiveData<List<PlaceEntity>> = repository.search(query)

    fun insert(place: PlaceEntity) = viewModelScope.launch { repository.insert(place) }
    fun update(place: PlaceEntity) = viewModelScope.launch { repository.update(place) }
    fun delete(place: PlaceEntity) = viewModelScope.launch { repository.delete(place) }
}
