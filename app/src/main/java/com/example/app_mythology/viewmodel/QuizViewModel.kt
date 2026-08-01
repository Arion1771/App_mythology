package com.example.app_mythology.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.app_mythology.database.AppDatabase
import com.example.app_mythology.database.ArtifactEntity
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.database.PlaceEntity
import com.example.app_mythology.quiz.ListItem
import com.example.app_mythology.quiz.ListThemeCatalog
import com.example.app_mythology.quiz.ThemeGroup
import com.example.app_mythology.quiz.resolveGroups
import com.example.app_mythology.repository.ArtifactRepository
import com.example.app_mythology.repository.EntiteRepository
import com.example.app_mythology.repository.PlaceRepository
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val entiteRepo   = EntiteRepository.getInstance(AppDatabase.getInstance(application))
    private val placeRepo    = PlaceRepository.getInstance(AppDatabase.getInstance(application))
    private val artifactRepo = ArtifactRepository.getInstance(AppDatabase.getInstance(application))

    // ── Quiz Entités / Artéfacts (mécanisme partagé : indice → nom) ─────────

    enum class QuizLevel { EASY, MEDIUM, HARD }

    private val _quizEntites  = MutableLiveData<List<EntiteEntity>>()
    val quizEntites: LiveData<List<EntiteEntity>> = _quizEntites

    private val _quizArtifacts = MutableLiveData<List<ArtifactEntity>>()
    val quizArtifacts: LiveData<List<ArtifactEntity>> = _quizArtifacts

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex

    private val _currentStep  = MutableLiveData(1)
    val currentStep: LiveData<Int> = _currentStep

    private val _score        = MutableLiveData(0.0)
    val score: LiveData<Double> = _score

    private val _maxScore = MutableLiveData(0.0)
    val maxScore: LiveData<Double> = _maxScore

    private val _quizFinished = MutableLiveData(false)
    val quizFinished: LiveData<Boolean> = _quizFinished

    // Résultats de chaque question : null, "green", "yellow", "red"
    private val _results = MutableLiveData<List<String?>>(emptyList())
    val results: LiveData<List<String?>> = _results

    private val _waitingForNext = MutableLiveData(false)
    val waitingForNext: LiveData<Boolean> = _waitingForNext

    // true dès qu'une réponse (juste ou fausse au 2e essai) a été donnée pour la question courante
    private val _answerRevealed = MutableLiveData(false)
    val answerRevealed: LiveData<Boolean> = _answerRevealed

    // Empêche le rechargement (et donc la remise à zéro) du quiz lors d'un
    // changement de configuration (ex. rotation de l'écran), le ViewModel
    // survivant à la recréation du fragment.
    private var entityQuizLoaded = false
    private var artifactQuizLoaded = false

    /**
     * Charge un quiz d'entités selon le niveau choisi :
     * - EASY   : 10 entités difficulty=1
     * - MEDIUM : 10 difficulty=1 + 10 difficulty=2
     * - HARD   : 10 difficulty=1 + 10 difficulty=2 + 10 difficulty=3
     */
    fun loadEntityQuiz(level: QuizLevel = QuizLevel.EASY) {
        // Ne charge le quiz qu'une seule fois : après une rotation, le quiz en
        // cours (index, score, résultats…) est conservé au lieu d'être réinitialisé.
        if (entityQuizLoaded) return
        entityQuizLoaded = true
        viewModelScope.launch {
            val pool = mutableListOf<EntiteEntity>()
            pool += entiteRepo.getRandomByDifficulty(1, 10)
            if (level == QuizLevel.MEDIUM || level == QuizLevel.HARD) {
                pool += entiteRepo.getRandomByDifficulty(2, 10)
            }
            if (level == QuizLevel.HARD) {
                pool += entiteRepo.getRandomByDifficulty(3, 10)
            }
            pool.shuffle()
            _quizEntites.value = pool
            resetQuizState(pool.sumOf { it.difficulty.toDouble() }, pool.size)
        }
    }

    /** Même principe que [loadEntityQuiz], pour les artéfacts. */
    fun loadArtifactQuiz(level: QuizLevel = QuizLevel.EASY) {
        if (artifactQuizLoaded) return
        artifactQuizLoaded = true
        viewModelScope.launch {
            val pool = mutableListOf<ArtifactEntity>()
            pool += artifactRepo.getRandomByDifficulty(1, 10)
            if (level == QuizLevel.MEDIUM || level == QuizLevel.HARD) {
                pool += artifactRepo.getRandomByDifficulty(2, 10)
            }
            if (level == QuizLevel.HARD) {
                pool += artifactRepo.getRandomByDifficulty(3, 10)
            }
            pool.shuffle()
            _quizArtifacts.value = pool
            resetQuizState(pool.sumOf { it.difficulty.toDouble() }, pool.size)
        }
    }

    private fun resetQuizState(maxScore: Double, poolSize: Int) {
        _currentIndex.value   = 0
        _currentStep.value    = 1
        _score.value          = 0.0
        _quizFinished.value   = false
        _maxScore.value       = maxScore
        _results.value        = MutableList(poolSize) { null }
        _waitingForNext.value = false
        _answerRevealed.value = false
    }

    /** Nom de la question courante, que ce soit un quiz d'entités ou d'artéfacts. */
    private fun currentQuizName(index: Int): String? =
        _quizArtifacts.value?.getOrNull(index)?.name ?: _quizEntites.value?.getOrNull(index)?.name

    private fun currentQuizDifficulty(index: Int): Double =
        (_quizArtifacts.value?.getOrNull(index)?.difficulty
            ?: _quizEntites.value?.getOrNull(index)?.difficulty ?: 1).toDouble()

    fun checkAnswer(input: String): Boolean {
        val name = currentQuizName(_currentIndex.value ?: 0) ?: return false
        return normalize(input) == normalize(name)
    }

    /**
     * Pas 1 correct  → +difficulty points, infos révélées, attend "suivant"
     * Pas 1 incorrect → passe au pas 2 (deuxième essai)
     * Pas 2 correct  → +difficulty/2 points, infos révélées, attend "suivant"
     * Pas 2 incorrect → 0 point, réponse révélée, attend "suivant"
     */
    fun submitAnswer(correct: Boolean) {
        val step       = _currentStep.value ?: 1
        val index      = _currentIndex.value ?: 0
        val difficulty = currentQuizDifficulty(index)

        if (correct) {
            val gained = if (step == 1) difficulty else difficulty / 2.0
            _score.value = (_score.value ?: 0.0) + gained
            updateResult(index, if (step == 1) "green" else "yellow")
            _answerRevealed.value = true
            _waitingForNext.value = true
        } else {
            if (step == 1) {
                _currentStep.value = 2
            } else {
                updateResult(index, "red")
                _answerRevealed.value = true
                _waitingForNext.value = true
            }
        }
    }

    fun nextAfterWrong() {
        _waitingForNext.value  = false
        _answerRevealed.value  = false
        advanceToNext()
    }

    private fun updateResult(index: Int, value: String) {
        val list = (_results.value ?: emptyList()).toMutableList()
        if (index < list.size) list[index] = value
        _results.value = list
    }

    private fun advanceToNext() {
        val poolSize = _quizArtifacts.value?.takeIf { it.isNotEmpty() }?.size
            ?: _quizEntites.value?.size ?: 0
        val next = (_currentIndex.value ?: 0) + 1
        if (next >= poolSize) {
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

    private val _placeWrongAttempts = MutableLiveData(0)
    val placeWrongAttempts: LiveData<Int> = _placeWrongAttempts

    private val _placeQuizFinished = MutableLiveData(false)
    val placeQuizFinished: LiveData<Boolean> = _placeQuizFinished

    private var placeQuizLoaded = false

    fun loadPlaceQuizzes() {
        // Comme pour le quiz d'entités : on préserve les lieux déjà trouvés
        // lors d'une rotation de l'écran.
        if (placeQuizLoaded) return
        placeQuizLoaded = true
        _foundIds.value = emptySet()
        _placeWrongAttempts.value = 0
        _placeQuizFinished.value = false
    }

    fun checkPlaceAnswer(input: String, places: List<PlaceEntity>): PlaceEntity? {
        val found = _foundIds.value ?: emptySet()
        return places.firstOrNull { p -> p.id !in found && normalize(input) == normalize(p.name) }
    }

    /** Marque un lieu trouvé et termine le quiz si tous les lieux ont été trouvés. */
    fun markPlaceFound(id: Int, totalCount: Int) {
        val updated = (_foundIds.value ?: emptySet()) + id
        _foundIds.value = updated
        if (updated.size >= totalCount) {
            _placeQuizFinished.value = true
        }
    }

    /**
     * Une réponse fausse consomme un essai parmi les [MAX_PLACE_ATTEMPTS] autorisés ;
     * le quiz se termine dès que ce nombre est atteint.
     */
    fun registerWrongPlaceAttempt() {
        val attempts = (_placeWrongAttempts.value ?: 0) + 1
        _placeWrongAttempts.value = attempts
        if (attempts >= MAX_PLACE_ATTEMPTS) {
            _placeQuizFinished.value = true
        }
    }

    companion object {
        const val MAX_PLACE_ATTEMPTS = 5
    }

    // ── Quiz QCM (Entités / Artéfacts, un seul essai, 4 choix) ──────────────

    private val _qcmEntites = MutableLiveData<List<EntiteEntity>>()
    val qcmEntites: LiveData<List<EntiteEntity>> = _qcmEntites

    private val _qcmArtifacts = MutableLiveData<List<ArtifactEntity>>()
    val qcmArtifacts: LiveData<List<ArtifactEntity>> = _qcmArtifacts

    private val _qcmIndex = MutableLiveData(0)
    val qcmIndex: LiveData<Int> = _qcmIndex

    private val _qcmChoices = MutableLiveData<List<String>>(emptyList())
    val qcmChoices: LiveData<List<String>> = _qcmChoices

    private val _qcmScore = MutableLiveData(0.0)
    val qcmScore: LiveData<Double> = _qcmScore

    private val _qcmMaxScore = MutableLiveData(0.0)
    val qcmMaxScore: LiveData<Double> = _qcmMaxScore

    private val _qcmResults = MutableLiveData<List<String?>>(emptyList())
    val qcmResults: LiveData<List<String?>> = _qcmResults

    // true dès qu'une réponse a été donnée pour la question courante (bascule vers l'écran de résultat)
    private val _qcmAnswerRevealed = MutableLiveData(false)
    val qcmAnswerRevealed: LiveData<Boolean> = _qcmAnswerRevealed

    private val _qcmFinished = MutableLiveData(false)
    val qcmFinished: LiveData<Boolean> = _qcmFinished

    private var qcmEntityQuizLoaded = false
    private var qcmArtifactQuizLoaded = false
    private var qcmAllEntites: List<EntiteEntity> = emptyList()
    private var qcmAllArtifacts: List<ArtifactEntity> = emptyList()

    fun loadEntityQcm(level: QuizLevel = QuizLevel.EASY) {
        if (qcmEntityQuizLoaded) return
        qcmEntityQuizLoaded = true
        viewModelScope.launch {
            qcmAllEntites = entiteRepo.getAllSync()
            val pool = mutableListOf<EntiteEntity>()
            pool += entiteRepo.getRandomByDifficulty(1, 10)
            if (level == QuizLevel.MEDIUM || level == QuizLevel.HARD) pool += entiteRepo.getRandomByDifficulty(2, 10)
            if (level == QuizLevel.HARD) pool += entiteRepo.getRandomByDifficulty(3, 10)
            pool.shuffle()
            _qcmEntites.value = pool
            resetQcmState(pool.sumOf { it.difficulty.toDouble() }, pool.size)
            buildQcmChoices(0)
        }
    }

    fun loadArtifactQcm(level: QuizLevel = QuizLevel.EASY) {
        if (qcmArtifactQuizLoaded) return
        qcmArtifactQuizLoaded = true
        viewModelScope.launch {
            qcmAllArtifacts = artifactRepo.getAllSync()
            val pool = mutableListOf<ArtifactEntity>()
            pool += artifactRepo.getRandomByDifficulty(1, 10)
            if (level == QuizLevel.MEDIUM || level == QuizLevel.HARD) pool += artifactRepo.getRandomByDifficulty(2, 10)
            if (level == QuizLevel.HARD) pool += artifactRepo.getRandomByDifficulty(3, 10)
            pool.shuffle()
            _qcmArtifacts.value = pool
            resetQcmState(pool.sumOf { it.difficulty.toDouble() }, pool.size)
            buildQcmChoices(0)
        }
    }

    private fun resetQcmState(maxScore: Double, poolSize: Int) {
        _qcmIndex.value = 0
        _qcmScore.value = 0.0
        _qcmMaxScore.value = maxScore
        _qcmResults.value = MutableList(poolSize) { null }
        _qcmFinished.value = false
        _qcmAnswerRevealed.value = false
    }

    private fun tagsOf(raw: String?): Set<String> = raw?.split(",")?.map { it.trim() }?.toSet() ?: emptySet()

    /** Score de proximité thématique entre deux entités, pour choisir des leurres plausibles. */
    private fun similarityScore(correct: EntiteEntity, other: EntiteEntity): Int {
        var s = (tagsOf(correct.tags) intersect tagsOf(other.tags)).size * 3
        if (other.mythology == correct.mythology) s += 2
        if (other.race == correct.race) s += 2
        if (correct.godType != null && correct.godType == other.godType) s += 1
        if (correct.monsterType != null && correct.monsterType == other.monsterType) s += 1
        if (correct.equivalentName == other.name || other.equivalentName == correct.name) s += 4
        return s
    }

    private fun similarityScore(correct: ArtifactEntity, other: ArtifactEntity): Int {
        var s = (tagsOf(correct.tags) intersect tagsOf(other.tags)).size * 3
        if (other.mythology == correct.mythology) s += 2
        if (other.artifactType == correct.artifactType) s += 2
        return s
    }

    private fun buildQcmChoices(index: Int) {
        val artifacts = _qcmArtifacts.value
        if (!artifacts.isNullOrEmpty()) {
            val correct = artifacts.getOrNull(index) ?: return
            val decoys = qcmAllArtifacts.filter { it.id != correct.id }
                .sortedByDescending { similarityScore(correct, it) }
                .take(12).shuffled().take(3)
            _qcmChoices.value = (listOf(correct.name) + decoys.map { it.name }).shuffled()
            return
        }
        val entites = _qcmEntites.value ?: return
        val correct = entites.getOrNull(index) ?: return
        val decoys = qcmAllEntites.filter { it.id != correct.id }
            .sortedByDescending { similarityScore(correct, it) }
            .take(12).shuffled().take(3)
        _qcmChoices.value = (listOf(correct.name) + decoys.map { it.name }).shuffled()
    }

    private fun currentQcmName(index: Int): String? =
        _qcmArtifacts.value?.getOrNull(index)?.name ?: _qcmEntites.value?.getOrNull(index)?.name

    private fun currentQcmDifficulty(index: Int): Double =
        (_qcmArtifacts.value?.getOrNull(index)?.difficulty
            ?: _qcmEntites.value?.getOrNull(index)?.difficulty ?: 1).toDouble()

    /** Un seul essai : point plein si correct, 0 sinon ; bascule vers l'écran de résultat de la question. */
    fun submitQcmAnswer(selected: String) {
        val index = _qcmIndex.value ?: 0
        val name = currentQcmName(index) ?: return
        val correct = normalize(selected) == normalize(name)
        if (correct) {
            _qcmScore.value = (_qcmScore.value ?: 0.0) + currentQcmDifficulty(index)
        }
        val results = (_qcmResults.value ?: emptyList()).toMutableList()
        if (index < results.size) results[index] = if (correct) "green" else "red"
        _qcmResults.value = results
        _qcmAnswerRevealed.value = true
    }

    /** Depuis l'écran de résultat de la question : passe à la suivante, ou termine le quiz. */
    fun nextQcmQuestion() {
        _qcmAnswerRevealed.value = false
        val poolSize = _qcmArtifacts.value?.takeIf { it.isNotEmpty() }?.size ?: _qcmEntites.value?.size ?: 0
        val next = (_qcmIndex.value ?: 0) + 1
        if (next >= poolSize) {
            _qcmFinished.value = true
        } else {
            _qcmIndex.value = next
            buildQcmChoices(next)
        }
    }

    // ── Quiz Liste (thème choisi, trouver toutes les entrées) ───────────────

    private val _listGroups = MutableLiveData<List<ThemeGroup<ListItem>>>(emptyList())
    val listGroups: LiveData<List<ThemeGroup<ListItem>>> = _listGroups

    private val _listFoundIds = MutableLiveData<Set<Int>>(emptySet())
    val listFoundIds: LiveData<Set<Int>> = _listFoundIds

    private val _listWrongAttempts = MutableLiveData(0)
    val listWrongAttempts: LiveData<Int> = _listWrongAttempts

    private val _listFinished = MutableLiveData(false)
    val listFinished: LiveData<Boolean> = _listFinished

    private var listMaxErrors = 3
    fun currentListMaxErrors() = listMaxErrors

    private var listQuizThemeId: String? = null

    fun loadListQuiz(themeId: String) {
        // Comme les autres quiz : ne recharge pas si déjà chargé pour ce thème
        // (préserve la progression lors d'une rotation d'écran).
        if (listQuizThemeId == themeId) return
        listQuizThemeId = themeId
        viewModelScope.launch {
            val theme = ListThemeCatalog.byId(themeId) ?: return@launch
            val entites = entiteRepo.getAllSync()
            val artifacts = artifactRepo.getAllSync()
            _listGroups.value = theme.resolveGroups(entites, artifacts)
            _listFoundIds.value = emptySet()
            _listWrongAttempts.value = 0
            _listFinished.value = false
            listMaxErrors = theme.maxErrors
        }
    }

    fun allListItems(): List<ListItem> = _listGroups.value?.flatMap { it.items } ?: emptyList()

    fun checkListAnswer(input: String): ListItem? {
        val found = _listFoundIds.value ?: emptySet()
        return allListItems().firstOrNull { it.id !in found && normalize(input) == normalize(it.name) }
    }

    fun markListFound(id: Int) {
        val updated = (_listFoundIds.value ?: emptySet()) + id
        _listFoundIds.value = updated
        if (updated.size >= allListItems().size) {
            _listFinished.value = true
        }
    }

    fun registerWrongListAttempt() {
        val attempts = (_listWrongAttempts.value ?: 0) + 1
        _listWrongAttempts.value = attempts
        if (attempts >= listMaxErrors) {
            _listFinished.value = true
        }
    }

    private fun normalize(s: String): String =
        java.text.Normalizer
            .normalize(s.trim().lowercase(), java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
}
