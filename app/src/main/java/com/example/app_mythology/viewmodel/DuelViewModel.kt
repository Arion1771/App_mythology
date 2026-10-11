package com.example.app_mythology.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app_mythology.database.AppDatabase
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.database.PlaceEntity
import com.example.app_mythology.quiz.FlatQuizRules
import com.example.app_mythology.quiz.pickQcmChoices
import com.example.app_mythology.repository.EntiteRepository
import com.example.app_mythology.repository.PlaceRepository
import kotlinx.coroutines.launch

/**
 * Mode duel multijoueur local (pass-and-play) : questions d'entités
 * (Classique, QCM), ou quiz Lieux à deux joueurs sur un seul écran.
 */
class DuelViewModel(application: Application) : AndroidViewModel(application) {

    private val appDb = AppDatabase.getInstance(application)
    private val entiteRepo = EntiteRepository.getInstance(appDb)
    private val placeRepo = PlaceRepository.getInstance(appDb)

    enum class DuelMode { CLASSIC, QCM, PLACES }

    class DuelPlayer(val name: String) {
        var score: Double = 0.0
        var questions: List<EntiteEntity> = emptyList()
        var results: MutableList<String?> = mutableListOf()
    }

    // ── Configuration (choisie sur les écrans de mise en place) ─────────────
    var mode: DuelMode = DuelMode.CLASSIC
    var level: QuizViewModel.QuizLevel = QuizViewModel.QuizLevel.EASY
    var sameQuestions: Boolean = true

    private val _players = MutableLiveData<List<DuelPlayer>>(emptyList())
    val players: LiveData<List<DuelPlayer>> = _players

    private val _questionCount = MutableLiveData(0)
    val questionCount: LiveData<Int> = _questionCount

    private val _currentPlayerIndex = MutableLiveData(0)
    val currentPlayerIndex: LiveData<Int> = _currentPlayerIndex

    private val _currentRound = MutableLiveData(0)
    val currentRound: LiveData<Int> = _currentRound

    // Essai 1 ou 2, mode Classique uniquement (toujours 1 en QCM).
    private val _currentStep = MutableLiveData(1)
    val currentStep: LiveData<Int> = _currentStep

    private val _qcmChoices = MutableLiveData<List<String>>(emptyList())
    val qcmChoices: LiveData<List<String>> = _qcmChoices

    // true dès qu'une réponse (juste ou fausse au dernier essai autorisé) a été donnée
    private val _answerRevealed = MutableLiveData(false)
    val answerRevealed: LiveData<Boolean> = _answerRevealed

    private val _duelFinished = MutableLiveData(false)
    val duelFinished: LiveData<Boolean> = _duelFinished

    private var qcmAllEntites: List<EntiteEntity> = emptyList()
    private var duelStarted = false

    fun setPlayerNames(names: List<String>) {
        _players.value = names.map { DuelPlayer(it) }
    }

    /** Construit les pools de questions et lance la partie. Ne fait rien si déjà lancée (survit à une rotation). */
    fun startDuel() {
        if (duelStarted) return
        duelStarted = true
        viewModelScope.launch {
            val players = _players.value ?: return@launch
            val perPlayer = FlatQuizRules.QUESTIONS_PER_QUIZ
            _questionCount.value = perPlayer
            val all = entiteRepo.getAllSync()
            if (mode == DuelMode.QCM) qcmAllEntites = all

            // 15 questions par joueur dans le niveau choisi, complétées par les
            // niveaux voisins s'il n'en contient pas assez (beaucoup de joueurs
            // en mode "questions différentes"). Le pool est trié niveau choisi
            // d'abord : le distribuer une carte à chaque joueur à tour de rôle
            // répartit équitablement les éventuelles questions d'appoint.
            val needed = perPlayer * (if (sameQuestions) 1 else players.size)
            val pool = FlatQuizRules.pickFromLevel(all, level.canonical, needed) { it.difficulty }

            if (sameQuestions) {
                val shared = pool.shuffled()
                players.forEach { it.questions = shared }
            } else {
                players.forEachIndexed { i, p ->
                    p.questions = pool.filterIndexed { j, _ -> j % players.size == i }.shuffled()
                }
            }
            players.forEach {
                it.score = 0.0
                it.results = MutableList(it.questions.size) { null }
            }
            _players.value = players

            _currentPlayerIndex.value = 0
            _currentRound.value = 0
            _currentStep.value = 1
            _answerRevealed.value = false
            _duelFinished.value = false
            if (mode == DuelMode.QCM) buildCurrentQcmChoices()
        }
    }

    fun currentPlayer(): DuelPlayer? = _players.value?.getOrNull(_currentPlayerIndex.value ?: 0)

    fun currentQuestion(): EntiteEntity? = currentPlayer()?.questions?.getOrNull(_currentRound.value ?: 0)

    private fun buildCurrentQcmChoices() {
        val correct = currentQuestion() ?: return
        _qcmChoices.value = pickQcmChoices(correct, qcmAllEntites)
    }

    fun checkClassicAnswer(input: String): Boolean {
        val name = currentQuestion()?.name ?: return false
        return normalize(input) == normalize(name)
    }

    /** Essai 1 correct → 2 points ; essai 2 correct → 1 point ; sinon 0 (voir [FlatQuizRules]). */
    fun submitClassicAnswer(correct: Boolean) {
        val player = currentPlayer() ?: return
        val round = _currentRound.value ?: 0
        val question = currentQuestion() ?: return
        val step = _currentStep.value ?: 1

        if (correct) {
            player.score += if (step == 1) FlatQuizRules.POINTS_PER_QUESTION else FlatQuizRules.POINTS_PER_QUESTION / 2
            player.results[round] = if (step == 1) "green" else "yellow"
            _players.value = _players.value
            _answerRevealed.value = true
        } else if (step == 1) {
            _currentStep.value = 2
        } else {
            player.results[round] = "red"
            _players.value = _players.value
            _answerRevealed.value = true
        }
    }

    /** Essai unique : 2 points si correct, 0 sinon. */
    fun submitQcmAnswer(selected: String) {
        val player = currentPlayer() ?: return
        val round = _currentRound.value ?: 0
        val question = currentQuestion() ?: return
        val correct = normalize(selected) == normalize(question.name)
        if (correct) player.score += FlatQuizRules.POINTS_PER_QUESTION
        player.results[round] = if (correct) "green" else "red"
        _players.value = _players.value
        _answerRevealed.value = true
    }

    /** true si le tour affiché (avant le clic sur "suivant") est le tout dernier de la partie. */
    fun isLastTurn(): Boolean {
        val players = _players.value ?: return false
        val lastPlayer = (_currentPlayerIndex.value ?: 0) == players.size - 1
        val lastRound = (_currentRound.value ?: 0) == (_questionCount.value ?: 0) - 1
        return lastPlayer && lastRound
    }

    /** Joueur suivant ; passe au tour suivant après le dernier joueur ; termine la partie après le dernier tour. */
    fun advanceTurn() {
        _answerRevealed.value = false
        _currentStep.value = 1
        val players = _players.value ?: return
        val nextPlayer = (_currentPlayerIndex.value ?: 0) + 1
        if (nextPlayer < players.size) {
            _currentPlayerIndex.value = nextPlayer
        } else {
            val nextRound = (_currentRound.value ?: 0) + 1
            if (nextRound >= (_questionCount.value ?: 0)) {
                _duelFinished.value = true
                return
            }
            _currentRound.value = nextRound
            _currentPlayerIndex.value = 0
        }
        if (mode == DuelMode.QCM) buildCurrentQcmChoices()
    }

    // ── Duel Lieux (2 joueurs, un essai chacun à tour de rôle) ─────────────

    /** Quiz Lieux joué : "yggdrasil" | "rivers" | "underworld" (mêmes listes que le quiz solo). */
    var placeQuizType: String = "yggdrasil"
        private set

    /** Lieu trouvé → index du joueur qui l'a trouvé. */
    private val _placeFoundBy = MutableLiveData<Map<Int, Int>>(emptyMap())
    val placeFoundBy: LiveData<Map<Int, Int>> = _placeFoundBy

    /** Erreurs de chaque joueur (même ordre que [players]). */
    private val _placeErrors = MutableLiveData<List<Int>>(emptyList())
    val placeErrors: LiveData<List<Int>> = _placeErrors

    /** Index du joueur qui doit répondre. */
    private val _placeTurn = MutableLiveData(0)
    val placeTurn: LiveData<Int> = _placeTurn

    private val _placeFinished = MutableLiveData(false)
    val placeFinished: LiveData<Boolean> = _placeFinished

    fun placesOf(type: String): LiveData<List<PlaceEntity>> = when (type) {
        "yggdrasil" -> placeRepo.yggdrasilRealms
        "rivers" -> placeRepo.hellRivers
        else -> placeRepo.underworldPlaces
    }

    /** (Re)lance une partie : appelé au choix du quiz, pas à la création de l'écran (survit à une rotation). */
    fun startPlaceDuel(type: String) {
        mode = DuelMode.PLACES
        placeQuizType = type
        val players = _players.value ?: return
        players.forEach { it.score = 0.0 }
        _placeFoundBy.value = emptyMap()
        _placeErrors.value = List(players.size) { 0 }
        _placeTurn.value = 0
        _placeFinished.value = false
    }

    /** Issue d'un essai en Duel Lieux. */
    sealed interface PlaceTry {
        data class Found(val place: PlaceEntity) : PlaceTry
        /** Lieu déjà trouvé : refusé, sans erreur ni changement de joueur. */
        data object AlreadyFound : PlaceTry
        data object Wrong : PlaceTry
        /** Essai ignoré (partie terminée ou lieux pas encore chargés). */
        data object Ignored : PlaceTry
    }

    /**
     * Un essai du joueur courant : un lieu trouvé lui est attribué, sinon il
     * prend une erreur (un lieu déjà trouvé est simplement refusé, le joueur
     * garde la main). La partie s'arrête quand tout est trouvé ou que chaque
     * joueur a atteint [MAX_PLACE_ERRORS] ; sinon la main passe à l'autre
     * joueur, sauf s'il est déjà éliminé (le joueur courant continue seul).
     */
    fun submitPlaceAnswer(input: String, places: List<PlaceEntity>): PlaceTry {
        if (_placeFinished.value == true || places.isEmpty()) return PlaceTry.Ignored
        val players = _players.value ?: return PlaceTry.Ignored
        val turn = _placeTurn.value ?: 0
        val foundBy = _placeFoundBy.value ?: emptyMap()
        val errors = (_placeErrors.value ?: emptyList()).toMutableList()

        if (places.any { it.id in foundBy && normalize(input) == normalize(it.name) }) return PlaceTry.AlreadyFound
        val match = places.firstOrNull { it.id !in foundBy && normalize(input) == normalize(it.name) }
        if (match != null) {
            _placeFoundBy.value = foundBy + (match.id to turn)
        } else {
            errors[turn] = errors[turn] + 1
            _placeErrors.value = errors
        }

        val allFound = (_placeFoundBy.value?.size ?: 0) >= places.size
        val everyoneOut = errors.all { it >= MAX_PLACE_ERRORS }
        if (allFound || everyoneOut) {
            finishPlaceDuel(players)
        } else {
            val next = (turn + 1) % players.size
            if (errors[next] < MAX_PLACE_ERRORS) _placeTurn.value = next
        }
        return if (match != null) PlaceTry.Found(match) else PlaceTry.Wrong
    }

    /** Points comptés en fin de partie : 1 par lieu trouvé. */
    private fun finishPlaceDuel(players: List<DuelPlayer>) {
        val foundBy = _placeFoundBy.value ?: emptyMap()
        players.forEachIndexed { i, p -> p.score = foundBy.values.count { it == i }.toDouble() }
        _players.value = players
        _placeFinished.value = true
    }

    companion object {
        const val MAX_PLACE_ERRORS = 3
    }

    private fun normalize(s: String): String =
        java.text.Normalizer
            .normalize(s.trim().lowercase(), java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
}
