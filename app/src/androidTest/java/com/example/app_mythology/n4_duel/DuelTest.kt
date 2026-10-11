package com.example.app_mythology.n4_duel

import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.DeviceStateRestoreRule
import com.example.app_mythology.QCM_CHOICE_BUTTONS
import com.example.app_mythology.R
import com.example.app_mythology.WRONG_ANSWER
import com.example.app_mythology.answerAndValidate
import com.example.app_mythology.assertCurrentDestination
import com.example.app_mythology.database.PlaceEntity
import com.example.app_mythology.formatScoreLikeApp
import com.example.app_mythology.graphViewModel
import com.example.app_mythology.onMain
import com.example.app_mythology.quiz.FlatQuizRules
import com.example.app_mythology.startDuel
import com.example.app_mythology.ui.MainActivity
import com.example.app_mythology.viewmodel.DuelViewModel
import com.example.app_mythology.waitFor
import com.example.app_mythology.waitForDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Mode Duel (Classique, QCM et Lieux) — branche Test-Non-Regression : mise
 * en place d'une partie à 2 joueurs (nombre, noms, type, difficulté, pool de
 * questions, annonce de tour), puis partie complète, tous les tours des deux
 * joueurs, jusqu'au classement final. Chaque réponse mène au récapitulatif ;
 * sont vérifiés le nombre et le niveau des questions, le statut de chaque
 * question pour chaque joueur (green / yellow / red), les scores finaux
 * (2 / 1 / 0 points) et le classement affiché. Duel Lieux : proposé à deux
 * joueurs seulement, alternance des essais, élimination à 3 erreurs, fin de
 * partie (3 erreurs chacun, ou tout trouvé) et points par lieu trouvé.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DuelTest {

    /**
     * Précaution : le duel n'écrit aujourd'hui rien sur l'appareil (ni rencontres,
     * ni niveaux internes, ni succès), mais l'état est tout de même restauré.
     */
    @get:Rule
    val deviceState = DeviceStateRestoreRule()

    /** Points obtenus et statut attendu pour une question. */
    private data class Outcome(val points: Double, val status: String)

    /**
     * Joue toute la partie : à chaque tour, [play] répond pour le joueur courant
     * et renvoie le résultat attendu ; vérifie ensuite les statuts de chaque
     * question, les scores finaux et le classement affiché.
     */
    private fun fullDuel(
        modeButton: Int, poolButton: Int, questionDest: Int,
        play: (vm: DuelViewModel, scenario: ActivityScenario<MainActivity>, player: Int, round: Int) -> Outcome,
    ) {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        val vm = startDuel(scenario, modeButton, poolButton, questionDest)
        val expectedScores = DoubleArray(2)
        val expectedStatuses = List(2) { mutableListOf<String>() }
        while (true) {
            scenario.waitForDestination(questionDest)
            val (player, round) = scenario.onMain { (vm.currentPlayerIndex.value ?: 0) to (vm.currentRound.value ?: 0) }
            val outcome = play(vm, scenario, player, round)
            expectedScores[player] += outcome.points
            expectedStatuses[player] += outcome.status

            scenario.waitForDestination(R.id.duelRecapFragment)
            onView(withId(R.id.tv_result_status)).check(matches(isDisplayed()))
            val wasLast = scenario.onMain { vm.isLastTurn() }
            onView(withId(R.id.btn_result_next)).perform(scrollTo(), click()) // joueur suivant / classement
            if (wasLast) break
            scenario.waitForDestination(R.id.duelAnnounceFragment)
            onView(withId(R.id.btn_duel_announce_start)).perform(click())
        }

        scenario.waitForDestination(R.id.duelResultFragment)
        val players = scenario.onMain { vm.players.value!! }
        players.forEachIndexed { i, p ->
            // 15 questions par joueur, toutes du niveau choisi (Facile : assez d'entités pour deux joueurs).
            assertEquals("Nombre de questions de ${p.name}", FlatQuizRules.QUESTIONS_PER_QUIZ, p.questions.size)
            assertTrue("Questions de ${p.name} hors du niveau Facile", p.questions.all { it.difficulty == 1 })
            assertEquals("Statuts des questions de ${p.name}", expectedStatuses[i], p.results)
            assertEquals("Score final de ${p.name}", expectedScores[i], p.score, 1e-9)
        }
        val expectedRows = players.sortedByDescending { it.score }
            .mapIndexed { i, p -> "${i + 1}. ${p.name} — ${formatScoreLikeApp(p.score)} pts" }
        val shownRows = scenario.onMain { activity ->
            val container = activity.findViewById<LinearLayout>(R.id.container_ranking)
            (0 until container.childCount).map { (container.getChildAt(it) as TextView).text.toString() }
        }
        assertEquals("Classement affiché", expectedRows, shownRows)
        scenario.close()
    }

    /**
     * Classique, mêmes questions pour les deux joueurs : bon au 1er essai (2
     * points) / bon au 2e essai (1 point) / deux essais faux (0), en rotation,
     * décalée d'un cran pour le joueur 2.
     */
    @Test
    fun t01_classicFullDuel() = fullDuel(
        R.id.btn_primary_1, R.id.btn_duel_pool_same, R.id.duelQuestionClassicFragment
    ) { vm, scenario, player, round ->
        onView(withId(R.id.et_answer)).check(matches(isDisplayed()))
        val question = scenario.onMain { vm.currentQuestion()!! }
        val (answers, outcome) = when ((round + player) % 3) {
            0 -> listOf(question.name) to Outcome(2.0, "green")
            1 -> listOf(WRONG_ANSWER, question.name) to Outcome(1.0, "yellow")
            else -> listOf(WRONG_ANSWER, WRONG_ANSWER) to Outcome(0.0, "red")
        }
        // Le passage au 2e essai vide le champ : chaque essai est une nouvelle saisie.
        answers.forEachIndexed { attempt, answer ->
            answerAndValidate(answer, R.id.et_answer, R.id.btn_validate, scrollable = true)
            if (attempt == 0 && answers.size == 2) waitFor { scenario.onMain { vm.currentStep.value } == 2 }
        }
        outcome
    }

    /**
     * QCM, questions différentes pour chaque joueur : joueur 1 bon un tour sur
     * deux (2 points / 0), joueur 2 toujours bon. Les deux sélections de
     * questions doivent être disjointes.
     */
    @Test
    fun t02_qcmFullDuel() = fullDuel(
        R.id.btn_primary_2, R.id.btn_duel_pool_different, R.id.duelQuestionQcmFragment
    ) { vm, scenario, player, round ->
        if (player == 0 && round == 0) {
            val (q1, q2) = scenario.onMain { vm.players.value!!.let { it[0].questions to it[1].questions } }
            assertTrue("Questions communes aux deux joueurs", q1.none { it in q2 })
        }
        val question = scenario.onMain { vm.currentQuestion()!! }
        waitFor { scenario.onMain { vm.qcmChoices.value.orEmpty().contains(question.name) } }
        QCM_CHOICE_BUTTONS.forEach { onView(withId(it)).check(matches(isDisplayed())) }
        val choices = scenario.onMain { vm.qcmChoices.value!! }
        val correct = player == 1 || round % 2 == 0
        val index = if (correct) choices.indexOf(question.name) else choices.indexOfFirst { it != question.name }
        onView(withId(QCM_CHOICE_BUTTONS[index])).perform(click())
        if (correct) Outcome(2.0, "green") else Outcome(0.0, "red")
    }

    // ── Duel Lieux ────────────────────────────────────────────────────────

    /** Accueil → choix du type de partie, avec [playerCount] joueurs aux noms par défaut. */
    private fun openModeChoice(scenario: ActivityScenario<MainActivity>, playerCount: Int) {
        onView(withId(R.id.btn_primary_3)).perform(click()) // Home -> Duel (nombre de joueurs)
        scenario.waitForDestination(R.id.duelPlayerCountFragment)
        scenario.onMain { it.findViewById<NumberPicker>(R.id.number_picker_players).value = playerCount }
        onView(withId(R.id.btn_duel_players_next)).perform(click())
        scenario.assertCurrentDestination(R.id.duelPlayerNamesFragment)
        onView(withId(R.id.btn_duel_names_next)).perform(click())
        scenario.assertCurrentDestination(R.id.duelModeChoiceFragment)
    }

    /** Lance le Duel Lieux à 2 joueurs sur le quiz [quizButton] ; renvoie le ViewModel et les lieux à trouver. */
    private fun startPlaceDuel(
        scenario: ActivityScenario<MainActivity>, quizButton: Int,
    ): Pair<DuelViewModel, List<PlaceEntity>> {
        openModeChoice(scenario, 2)
        onView(withId(R.id.btn_primary_3)).check(matches(isDisplayed())).perform(click()) // Lieux
        scenario.assertCurrentDestination(R.id.duelPlaceChoiceFragment)
        onView(withId(quizButton)).perform(click())
        scenario.waitForDestination(R.id.duelPlaceFragment)
        val vm = scenario.graphViewModel<DuelViewModel>(R.id.duel_graph)
        waitFor { scenario.onMain { vm.placesOf(vm.placeQuizType).value }.orEmpty().isNotEmpty() }
        val places = scenario.onMain { vm.placesOf(vm.placeQuizType).value!! }
        return vm to places
    }

    /**
     * Un essai du joueur attendu ([expectedPlayer], vérifié dans le ViewModel
     * et sur le nom affiché au-dessus du champ), puis attente de sa prise en compte.
     */
    private fun placeTry(
        scenario: ActivityScenario<MainActivity>, vm: DuelViewModel, expectedPlayer: Int, answer: String,
    ) {
        assertEquals("Joueur attendu", expectedPlayer, scenario.onMain { vm.placeTurn.value })
        onView(withId(R.id.tv_duel_place_player)).check(matches(withText("Au tour de Joueur ${expectedPlayer + 1}")))
        val before = scenario.onMain { (vm.placeFoundBy.value?.size ?: 0) + (vm.placeErrors.value?.sum() ?: 0) }
        answerAndValidate(answer, R.id.et_place_answer, R.id.btn_place_validate, scrollable = false)
        waitFor { scenario.onMain { (vm.placeFoundBy.value?.size ?: 0) + (vm.placeErrors.value?.sum() ?: 0) } == before + 1 }
    }

    /** Essai refusé (champ vide, lieu déjà trouvé) : ni lieu ni erreur en plus, le même joueur garde la main. */
    private fun rejectedPlaceTry(
        scenario: ActivityScenario<MainActivity>, vm: DuelViewModel, expectedPlayer: Int, answer: String,
    ) {
        val before = scenario.onMain { vm.placeFoundBy.value to vm.placeErrors.value }
        answerAndValidate(answer, R.id.et_place_answer, R.id.btn_place_validate, scrollable = false)
        assertEquals("Essai « $answer » refusé sans effet", before, scenario.onMain { vm.placeFoundBy.value to vm.placeErrors.value })
        assertEquals("Joueur qui garde la main", expectedPlayer, scenario.onMain { vm.placeTurn.value })
    }

    private fun playerColor(scenario: ActivityScenario<MainActivity>, player: Int): Int = scenario.onMain {
        ContextCompat.getColor(it, if (player == 0) R.color.duel_player_1 else R.color.duel_player_2)
    }

    /** Couleur du nom affiché sur la carte du lieu [placeId]. */
    private fun cardNameColor(scenario: ActivityScenario<MainActivity>, placeId: Int): Int = scenario.onMain { activity ->
        val grid = activity.findViewById<GridLayout>(R.id.grid_places)
        val card = (0 until grid.childCount).map { grid.getChildAt(it) }.first { it.tag == placeId }
        card.findViewById<TextView>(R.id.tv_card_name).currentTextColor
    }

    /** Vérifie le classement final affiché après le bouton « Classement final ». */
    private fun checkPlaceRanking(scenario: ActivityScenario<MainActivity>, expected: List<String>) {
        onView(withId(R.id.layout_place_result)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_duel_place_ranking)).perform(click())
        scenario.waitForDestination(R.id.duelResultFragment)
        val shownRows = scenario.onMain { activity ->
            val container = activity.findViewById<LinearLayout>(R.id.container_ranking)
            (0 until container.childCount).map { (container.getChildAt(it) as TextView).text.toString() }
        }
        assertEquals("Classement affiché", expected, shownRows)
    }

    /** Le type de partie Lieux n'est proposé qu'à deux joueurs. */
    @Test
    fun t03_placeDuelOnlyOfferedToTwoPlayers() {
        for ((count, shown) in listOf(3 to false, 2 to true)) {
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            openModeChoice(scenario, count)
            onView(withId(R.id.btn_primary_1)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_primary_2)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_primary_3)).check(
                matches(withEffectiveVisibility(if (shown) Visibility.VISIBLE else Visibility.GONE))
            )
            scenario.close()
        }
    }

    /**
     * Arbre Monde : un essai chacun à tour de rôle ; un champ vide ou un lieu
     * déjà trouvé est refusé sans erreur ; le joueur 1 atteint ses 3 erreurs
     * en premier et passe ensuite son tour, le joueur 2 continue seul jusqu'à
     * sa 3e erreur, qui termine la partie. Points : 1 par lieu trouvé ; nom du
     * joueur courant et des lieux trouvés à la couleur du joueur.
     */
    @Test
    fun t04_placeDuelEndsWhenBothPlayersHaveThreeErrors() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        val (vm, places) = startPlaceDuel(scenario, R.id.btn_primary_1)
        assertTrue("Arbre Monde doit compter au moins 4 lieux", places.size >= 4)

        val turnColor = { scenario.onMain { it.findViewById<TextView>(R.id.tv_duel_place_player).currentTextColor } }
        assertEquals("Couleur du joueur 1 à son tour", playerColor(scenario, 0), turnColor())
        rejectedPlaceTry(scenario, vm, 0, "")       // J1 : champ vide refusé
        placeTry(scenario, vm, 0, WRONG_ANSWER)     // J1 : 1 erreur
        assertEquals("Couleur du joueur 2 à son tour", playerColor(scenario, 1), turnColor())
        placeTry(scenario, vm, 1, places[0].name)   // J2 trouve
        placeTry(scenario, vm, 0, places[1].name)   // J1 trouve
        placeTry(scenario, vm, 1, WRONG_ANSWER)     // J2 : 1 erreur
        placeTry(scenario, vm, 0, WRONG_ANSWER)     // J1 : 2 erreurs
        placeTry(scenario, vm, 1, places[2].name)   // J2 trouve
        rejectedPlaceTry(scenario, vm, 0, places[2].name) // J1 : lieu déjà trouvé, refusé sans erreur
        placeTry(scenario, vm, 0, WRONG_ANSWER)     // J1 : 3 erreurs, éliminé
        assertEquals(listOf(3, 1), scenario.onMain { vm.placeErrors.value })
        placeTry(scenario, vm, 1, WRONG_ANSWER)     // J2 continue seul : 2 erreurs
        placeTry(scenario, vm, 1, places[3].name)   // J2 trouve
        assertEquals(false, scenario.onMain { vm.placeFinished.value })
        placeTry(scenario, vm, 1, WRONG_ANSWER)     // J2 : 3 erreurs, fin de partie

        waitFor { scenario.onMain { vm.placeFinished.value } == true }
        assertEquals(mapOf(places[0].id to 1, places[1].id to 0, places[2].id to 1, places[3].id to 1),
            scenario.onMain { vm.placeFoundBy.value })
        scenario.onMain { vm.placeFoundBy.value!! }.forEach { (placeId, finder) ->
            assertEquals("Couleur du lieu $placeId", playerColor(scenario, finder), cardNameColor(scenario, placeId))
        }
        onView(withId(R.id.layout_place_input)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        checkPlaceRanking(scenario, listOf("1. Joueur 2 — 3 pts", "2. Joueur 1 — 1 pts"))
        scenario.close()
    }

    /** Fleuves de l'Enfer : la partie se termine dès que tous les lieux sont trouvés, sans erreur. */
    @Test
    fun t05_placeDuelEndsWhenEverythingIsFound() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        val (vm, places) = startPlaceDuel(scenario, R.id.btn_primary_2)
        places.forEachIndexed { i, place -> placeTry(scenario, vm, i % 2, place.name) }

        waitFor { scenario.onMain { vm.placeFinished.value } == true }
        assertEquals(listOf(0, 0), scenario.onMain { vm.placeErrors.value })
        val p1 = (places.size + 1) / 2
        val p2 = places.size / 2
        checkPlaceRanking(scenario, listOf("1. Joueur 1 — $p1 pts", "2. Joueur 2 — $p2 pts"))
        scenario.close()
    }
}
