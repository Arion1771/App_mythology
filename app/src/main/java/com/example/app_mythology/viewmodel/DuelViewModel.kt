package com.example.app_mythology.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app_mythology.database.AppDatabase
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.quiz.pickQcmChoices
import com.example.app_mythology.repository.EntiteRepository
import kotlinx.coroutines.launch

/** Mode duel multijoueur local (pass-and-play), entités uniquement. */
class DuelViewModel(application: Application) : AndroidViewModel(application) {

    private val entiteRepo = EntiteRepository.getInstance(AppDatabase.getInstance(application))

    enum class DuelMode { CLASSIC, QCM }

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
            val perPlayer = when (level) {
                QuizViewModel.QuizLevel.EASY -> 10
                QuizViewModel.QuizLevel.MEDIUM -> 20
                QuizViewModel.QuizLevel.HARD -> 30
            }
            _questionCount.value = perPlayer
            if (mode == DuelMode.QCM) qcmAllEntites = entiteRepo.getAllSync()

            // Une seule requête RANDOM() LIMIT n par palier ne renvoie jamais deux fois
            // la même ligne : demander n * nombre de joueurs suffit à obtenir des
            // segments disjoints par joueur en mode "questions différentes", sans
            // nouvelle requête DAO dédiée.
            val multiplier = if (sameQuestions) 1 else players.size
            val pool = mutableListOf<EntiteEntity>()
            pool += entiteRepo.getRandomByDifficulty(1, 10 * multiplier)
            if (level != QuizViewModel.QuizLevel.EASY) pool += entiteRepo.getRandomByDifficulty(2, 10 * multiplier)
            if (level == QuizViewModel.QuizLevel.HARD) pool += entiteRepo.getRandomByDifficulty(3, 10 * multiplier)
            pool.shuffle()

            if (sameQuestions) {
                val shared = pool.take(perPlayer)
                players.forEach { it.questions = shared }
            } else {
                players.forEachIndexed { i, p -> p.questions = pool.drop(i * perPlayer).take(perPlayer) }
            }
            players.forEach { it.results = MutableList(it.questions.size) { null } }
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

    /** Essai 1 correct → points pleins ; essai 2 correct → moitié ; sinon 0. */
    fun submitClassicAnswer(correct: Boolean) {
        val player = currentPlayer() ?: return
        val round = _currentRound.value ?: 0
        val question = currentQuestion() ?: return
        val step = _currentStep.value ?: 1

        if (correct) {
            player.score += if (step == 1) question.difficulty.toDouble() else question.difficulty / 2.0
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

    /** Essai unique : points pleins si correct, 0 sinon. */
    fun submitQcmAnswer(selected: String) {
        val player = currentPlayer() ?: return
        val round = _currentRound.value ?: 0
        val question = currentQuestion() ?: return
        val correct = normalize(selected) == normalize(question.name)
        if (correct) player.score += question.difficulty.toDouble()
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

    private fun normalize(s: String): String =
        java.text.Normalizer
            .normalize(s.trim().lowercase(), java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
}
