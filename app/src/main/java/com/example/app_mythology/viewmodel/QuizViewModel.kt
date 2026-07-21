package com.example.app_mythology.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.app_mythology.database.AppDatabase
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.database.PlaceEntity
import com.example.app_mythology.repository.EntiteRepository
import com.example.app_mythology.repository.PlaceRepository
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val entiteRepo = EntiteRepository.getInstance(AppDatabase.getInstance(application))
    private val placeRepo = PlaceRepository.getInstance(AppDatabase.getInstance(application))

    // ─── Quiz Entités ──────────────────────────────────────────────────────

    private val _quizEntites = MutableLiveData<List<EntiteEntity>>()
    val quizEntites: LiveData<List<EntiteEntity>> = _quizEntites

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex

    // Pas courant : 1 = premier essai (mytho+domaine), 2 = second essai (tout sauf nom)
    private val _currentStep = MutableLiveData(1)
    val currentStep: LiveData<Int> = _currentStep

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _quizFinished = MutableLiveData(false)
    val quizFinished: LiveData<Boolean> = _quizFinished

    fun loadEntityQuiz() = viewModelScope.launch {
        _quizEntites.value = entiteRepo.getRandomForQuiz()
        _currentIndex.value = 0
        _currentStep.value = 1
        _score.value = 0
        _quizFinished.value = false
    }

    /**
     * Vérifie la réponse en ignorant accents et casse.
     * Retourne true si correcte.
     */
    fun checkAnswer(input: String): Boolean {
        val entities = _quizEntites.value ?: return false
        val current = entities.getOrNull(_currentIndex.value ?: 0) ?: return false
        return normalize(input) == normalize(current.name)
    }

    /**
     * Appelé après validation d'une réponse.
     * [correct] = si l'utilisateur a trouvé le nom.
     */
    fun submitAnswer(correct: Boolean) {
        val step = _currentStep.value ?: 1
        if (correct) {
            _score.value = (_score.value ?: 0) + if (step == 1) 2 else 1
            advanceToNext()
        } else {
            if (step == 1) {
                // Passer au second pas : afficher tout sauf le nom
                _currentStep.value = 2
            } else {
                // Pas de points, passer à l'entité suivante
                advanceToNext()
            }
        }
    }

    private fun advanceToNext() {
        val next = (_currentIndex.value ?: 0) + 1
        if (next >= (_quizEntites.value?.size ?: 10)) {
            _quizFinished.value = true
        } else {
            _currentIndex.value = next
            _currentStep.value = 1
        }
    }

    // ─── Quiz Lieux ────────────────────────────────────────────────────────

    private val _yggdrasilRealms = MutableLiveData<List<PlaceEntity>>()
    val yggdrasilRealms: LiveData<List<PlaceEntity>> = _yggdrasilRealms

    private val _hellRivers = MutableLiveData<List<PlaceEntity>>()
    val hellRivers: LiveData<List<PlaceEntity>> = _hellRivers

    private val _underworldPlaces = MutableLiveData<List<PlaceEntity>>()
    val underworldPlaces: LiveData<List<PlaceEntity>> = _underworldPlaces

    // Ensemble des noms déjà trouvés (par quiz de lieu)
    private val _foundIds = MutableLiveData<Set<Int>>(emptySet())
    val foundIds: LiveData<Set<Int>> = _foundIds

    fun loadPlaceQuizzes() {
        placeRepo.yggdrasilRealms.observeForever { _yggdrasilRealms.value = it }
        placeRepo.hellRivers.observeForever { _hellRivers.value = it }
        placeRepo.underworldPlaces.observeForever { _underworldPlaces.value = it }
        _foundIds.value = emptySet()
    }

    /**
     * Vérifie si le nom saisi correspond à un lieu non encore trouvé dans la liste donnée.
     * Retourne l'id du lieu trouvé, ou null.
     */
    fun checkPlaceAnswer(input: String, places: List<PlaceEntity>): PlaceEntity? {
        val found = _foundIds.value ?: emptySet()
        return places.firstOrNull { p ->
            p.id !in found && normalize(input) == normalize(p.name)
        }
    }

    fun markPlaceFound(id: Int) {
        val current = _foundIds.value ?: emptySet()
        _foundIds.value = current + id
    }

    // ─── Utilitaire ────────────────────────────────────────────────────────

    private fun normalize(s: String): String {
        return java.text.Normalizer
            .normalize(s.trim().lowercase(), java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }
}
