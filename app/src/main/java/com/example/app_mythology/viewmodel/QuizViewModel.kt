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
    private val placeRepo  = PlaceRepository.getInstance(AppDatabase.getInstance(application))

    // ── Quiz Entités ───────────────────────────────────────────────────────

    enum class QuizLevel { EASY, MEDIUM, HARD }

    private val _quizEntites  = MutableLiveData<List<EntiteEntity>>()
    val quizEntites: LiveData<List<EntiteEntity>> = _quizEntites

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex

    private val _currentStep  = MutableLiveData(1)
    val currentStep: LiveData<Int> = _currentStep

    private val _score        = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _quizFinished = MutableLiveData(false)
    val quizFinished: LiveData<Boolean> = _quizFinished

    private val _maxScore = MutableLiveData(20)
    val maxScore: LiveData<Int> = _maxScore

    // Résultats de chaque question : null, "green", "yellow", "red"
    private val _results = MutableLiveData<List<String?>>(emptyList())
    val results: LiveData<List<String?>> = _results

    private val _waitingForNext = MutableLiveData(false)
    val waitingForNext: LiveData<Boolean> = _waitingForNext

    /**
     * Charge un quiz selon le niveau choisi :
     * - EASY   : 10 entités difficulty=1
     * - MEDIUM : 10 difficulty=1 + 10 difficulty=2
     * - HARD   : 10 difficulty=1 + 10 difficulty=2 + 10 difficulty=3
     */
    fun loadEntityQuiz(level: QuizLevel = QuizLevel.EASY) = viewModelScope.launch {
        val pool = mutableListOf<EntiteEntity>()
        pool += entiteRepo.getRandomByDifficulty(1, 10)
        if (level == QuizLevel.MEDIUM || level == QuizLevel.HARD) {
            pool += entiteRepo.getRandomByDifficulty(2, 10)
        }
        if (level == QuizLevel.HARD) {
            pool += entiteRepo.getRandomByDifficulty(3, 10)
        }
        pool.shuffle()

        _quizEntites.value   = pool
        _currentIndex.value  = 0
        _currentStep.value   = 1
        _score.value         = 0
        _quizFinished.value  = false
        _maxScore.value       = pool.size * 2
        _results.value       = MutableList(pool.size) { null }
        _waitingForNext.value = false
    }

    fun checkAnswer(input: String): Boolean {
        val entities = _quizEntites.value ?: return false
        val current  = entities.getOrNull(_currentIndex.value ?: 0) ?: return false
        return normalize(input) == normalize(current.name)
    }

    fun submitAnswer(correct: Boolean) {
        val step  = _currentStep.value ?: 1
        val index = _currentIndex.value ?: 0
        if (correct) {
            _score.value = (_score.value ?: 0) + if (step == 1) 2 else 1
            updateResult(index, if (step == 1) "green" else "yellow")
            _waitingForNext.value = false
            advanceToNext()
        } else {
            if (step == 1) {
                _currentStep.value = 2
            } else {
                updateResult(index, "red")
                _waitingForNext.value = true
            }
        }
    }

    fun nextAfterWrong() {
        _waitingForNext.value = false
        advanceToNext()
    }

    private fun updateResult(index: Int, value: String) {
        val list = (_results.value ?: emptyList()).toMutableList()
        if (index < list.size) list[index] = value
        _results.value = list
    }

    private fun advanceToNext() {
        val next = (_currentIndex.value ?: 0) + 1
        if (next >= (_quizEntites.value?.size ?: 0)) {
            _quizFinished.value = true
        } else {
            _currentIndex.value = next
            _currentStep.value  = 1
        }
    }

    // ── Quiz Lieux ─────────────────────────────────────────────────────────

    val yggdrasilRealms:  LiveData<List<PlaceEntity>> = placeRepo.yggdrasilRealms
    val hellRivers:       LiveData<List<PlaceEntity>> = placeRepo.hellRivers
    val underworldPlaces: LiveData<List<PlaceEntity>> = placeRepo.underworldPlaces

    private val _foundIds = MutableLiveData<Set<Int>>(emptySet())
    val foundIds: LiveData<Set<Int>> = _foundIds

    fun loadPlaceQuizzes() { _foundIds.value = emptySet() }

    fun checkPlaceAnswer(input: String, places: List<PlaceEntity>): PlaceEntity? {
        val found = _foundIds.value ?: emptySet()
        return places.firstOrNull { p -> p.id !in found && normalize(input) == normalize(p.name) }
    }

    fun markPlaceFound(id: Int) {
        _foundIds.value = (_foundIds.value ?: emptySet()) + id
    }

    private fun normalize(s: String): String =
        java.text.Normalizer
            .normalize(s.trim().lowercase(), java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
}
