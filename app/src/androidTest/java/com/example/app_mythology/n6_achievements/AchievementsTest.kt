package com.example.app_mythology.n6_achievements

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.app_mythology.DeviceStateRestoreRule
import com.example.app_mythology.QCM_CHOICE_BUTTONS
import com.example.app_mythology.R
import com.example.app_mythology.WRONG_ANSWER
import com.example.app_mythology.achievements.AchievementCatalog
import com.example.app_mythology.achievements.AchievementManager
import com.example.app_mythology.answerAndValidate
import com.example.app_mythology.assertCurrentDestination
import com.example.app_mythology.currentFragmentQuizViewModel
import com.example.app_mythology.database.AppDatabase
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.database.PlaceEntity
import com.example.app_mythology.graphViewModel
import com.example.app_mythology.onMain
import com.example.app_mythology.ui.MainActivity
import com.example.app_mythology.viewmodel.QuizViewModel
import com.example.app_mythology.waitFor
import com.example.app_mythology.waitForDestination
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Obtention des succès — branche Test-Non-Regression. Chaque test part d'un
 * appareil sans aucun succès (la règle [DeviceStateRestoreRule] restaure
 * ensuite ceux du joueur), joue les écrans réels quand c'est possible, et
 * vérifie exactement quels succès sont débloqués :
 * - paliers « sans faute » des quiz Classique/QCM selon le niveau choisi,
 *   succès « tout faux » ;
 * - quiz Lieux (moins de 3 erreurs) et méta-succès des trois lieux ;
 * - thème du mode Liste complété ;
 * - succès d'entités nommées et de collection : les questions étant tirées
 *   au hasard, la règle du ViewModel est appelée directement (méthodes
 *   privées, par réflexion) avec les vraies entités de la base.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class AchievementsTest {

    @get:Rule
    val deviceState = DeviceStateRestoreRule()

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    /** Part d'un appareil sans aucun succès ni entité obtenue (restaurés après le test). */
    @Before
    fun clearAchievements() {
        context.getSharedPreferences("achievements", Context.MODE_PRIVATE).edit().clear().commit()
    }

    private fun unlocked(): Set<String> = AchievementManager.unlockedIds()

    private fun assertUnlockedExactly(expected: Set<String>, ignoring: (String) -> Boolean = { false }) {
        assertEquals("Succès débloqués", expected, unlocked().filterNot(ignoring).toSet())
    }

    /** Succès d'entités pouvant se débloquer au passage en répondant juste (questions tirées au hasard). */
    private fun isEntityBonus(id: String) = id.startsWith("entity_") || id.startsWith("collection_")

    // ── Quiz Classique / QCM ──────────────────────────────────────────────

    private class Domain(
        val qcm: Boolean, val artifact: Boolean,
        val modeButton: Int, val domainButton: Int,
        val quizDest: Int, val resultDest: Int, val graphId: Int,
    )

    private val classicArtifacts = Domain(
        false, true, R.id.btn_primary_2, R.id.btn_primary_2,
        R.id.quizArtifactFragment, R.id.quizArtifactResultFragment, R.id.quiz_artifact_graph,
    )
    private val qcmEntities = Domain(
        true, false, R.id.btn_primary_1, R.id.btn_primary_1,
        R.id.quizEntityQcmFragment, R.id.quizEntityQcmResultFragment, R.id.quiz_entity_qcm_graph,
    )
    private val qcmArtifacts = Domain(
        true, true, R.id.btn_primary_1, R.id.btn_primary_2,
        R.id.quizArtifactQcmFragment, R.id.quizArtifactQcmResultFragment, R.id.quiz_artifact_qcm_graph,
    )

    /** Joue tout un quiz au niveau [levelButton], toujours juste ([correct]) ou toujours faux. */
    private fun playWholeQuiz(domain: Domain, levelButton: Int, correct: Boolean) {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(domain.modeButton)).perform(click())  // Classique / QCM
        onView(withId(domain.domainButton)).perform(click())
        onView(withId(levelButton)).perform(click())
        scenario.waitForDestination(domain.quizDest)
        val vm = scenario.graphViewModel<QuizViewModel>(domain.graphId)

        val names: () -> List<String> = {
            scenario.onMain {
                when {
                    domain.qcm && domain.artifact -> vm.qcmArtifacts.value.orEmpty().map { it.name }
                    domain.qcm -> vm.qcmEntites.value.orEmpty().map { it.name }
                    domain.artifact -> vm.quizArtifacts.value.orEmpty().map { it.name }
                    else -> vm.quizEntites.value.orEmpty().map { it.name }
                }
            }
        }
        waitFor { names().isNotEmpty() }
        names().forEachIndexed { i, name ->
            scenario.waitForDestination(domain.quizDest)
            if (domain.qcm) {
                waitFor { scenario.onMain { vm.qcmIndex.value == i && vm.qcmChoices.value.orEmpty().contains(name) } }
                val choices = scenario.onMain { vm.qcmChoices.value!! }
                val index = if (correct) choices.indexOf(name) else choices.indexOfFirst { it != name }
                onView(withId(QCM_CHOICE_BUTTONS[index])).perform(click())
            } else {
                waitFor { scenario.onMain { vm.currentIndex.value == i && vm.currentStep.value == 1 } }
                if (correct) {
                    answerAndValidate(name, R.id.et_answer, R.id.btn_validate, scrollable = true)
                } else {
                    answerAndValidate(WRONG_ANSWER, R.id.et_answer, R.id.btn_validate, scrollable = true)
                    waitFor { scenario.onMain { vm.currentStep.value } == 2 }
                    answerAndValidate(WRONG_ANSWER, R.id.et_answer, R.id.btn_validate, scrollable = true)
                }
            }
            scenario.waitForDestination(domain.resultDest)
            onView(withId(R.id.btn_result_next)).perform(scrollTo(), click())
        }
        waitFor { scenario.onMain { if (domain.qcm) vm.qcmFinished.value else vm.quizFinished.value } == true }
        scenario.close()
    }

    @Test
    fun t01_perfectEasyClassicArtifactQuizUnlocksEasyTierOnly() {
        playWholeQuiz(classicArtifacts, R.id.btn_level_easy, correct = true)
        assertUnlockedExactly(setOf("classic_artifact_10"))
    }

    /** Verrou de V4.3.11 : un quiz d'entités Facile (15 questions) débloque bien le palier Facile. */
    @Test
    fun t02_perfectEasyQcmEntityQuizUnlocksEasyTierOnly() {
        playWholeQuiz(qcmEntities, R.id.btn_level_easy, correct = true)
        assertUnlockedExactly(setOf("qcm_entity_10"), ignoring = ::isEntityBonus)
    }

    @Test
    fun t03_perfectHardQcmArtifactQuizUnlocksHardTierOnly() {
        playWholeQuiz(qcmArtifacts, R.id.btn_level_hard, correct = true)
        assertUnlockedExactly(setOf("qcm_artifact_60"))
    }

    @Test
    fun t04_allWrongQuizUnlocksAllWrongOnly() {
        playWholeQuiz(classicArtifacts, R.id.btn_level_medium, correct = false)
        assertUnlockedExactly(setOf("all_wrong"))
    }

    // ── Quiz Lieux ───────────────────────────────────────────────────────

    /** Joue un quiz Lieux : [errors] réponses fausses, puis tous les lieux trouvés. */
    private fun playPlaceQuiz(button: Int, destination: Int, errors: Int, places: (QuizViewModel) -> LiveData<List<PlaceEntity>>) {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(R.id.btn_primary_2)).perform(click()) // Classique
        onView(withId(R.id.btn_primary_3)).perform(click()) // Lieux
        onView(withId(button)).perform(click())
        scenario.waitForDestination(destination)
        val vm = scenario.currentFragmentQuizViewModel()
        waitFor { scenario.onMain { places(vm).value }.orEmpty().isNotEmpty() }
        repeat(errors) { answerAndValidate(WRONG_ANSWER, R.id.et_place_answer, R.id.btn_place_validate, scrollable = false) }
        scenario.onMain { places(vm).value!! }.forEach {
            answerAndValidate(it.name, R.id.et_place_answer, R.id.btn_place_validate, scrollable = false)
        }
        waitFor { scenario.onMain { vm.placeQuizFinished.value } == true }
        assertEquals("Erreurs comptées", errors, scenario.onMain { vm.placeWrongAttempts.value })
        scenario.close()
    }

    private fun playYggdrasil(errors: Int) =
        playPlaceQuiz(R.id.btn_primary_1, R.id.quizYggdrasilFragment, errors) { it.yggdrasilRealms }
    private fun playRivers(errors: Int) =
        playPlaceQuiz(R.id.btn_primary_2, R.id.quizRiversFragment, errors) { it.hellRivers }
    private fun playUnderworld(errors: Int) =
        playPlaceQuiz(R.id.btn_primary_3, R.id.quizUnderworldFragment, errors) { it.underworldPlaces }

    @Test
    fun t05_placeQuizNeedsFewerThanThreeErrors() {
        playYggdrasil(errors = 3)
        assertUnlockedExactly(emptySet())
        playYggdrasil(errors = 2)
        assertUnlockedExactly(setOf("place_yggdrasil"))
    }

    @Test
    fun t06_allThreePlaceQuizzesUnlockTheMetaAchievement() {
        playRivers(errors = 0)
        playUnderworld(errors = 1)
        assertUnlockedExactly(setOf("place_rivers", "place_underworld"))
        playYggdrasil(errors = 0)
        assertUnlockedExactly(setOf("place_rivers", "place_underworld", "place_yggdrasil", "place_all"))
    }

    // ── Mode Liste ───────────────────────────────────────────────────────

    @Test
    fun t07_completingAListThemeUnlocksItsAchievement() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_2)).perform(click()) // Quizz
        onView(withId(R.id.btn_primary_3)).perform(click()) // Liste
        scenario.assertCurrentDestination(R.id.quizListChoiceFragment)
        onView(withText("Mythologie shinto")).perform(scrollTo(), click())
        scenario.waitForDestination(R.id.quizListFragment)
        val vm = scenario.currentFragmentQuizViewModel()
        waitFor { scenario.onMain { vm.allListItems() }.isNotEmpty() }
        val items = scenario.onMain { vm.allListItems() }

        items.dropLast(1).forEach { answerAndValidate(it.name, R.id.et_list_answer, R.id.btn_list_validate, scrollable = false) }
        assertUnlockedExactly(emptySet(), ignoring = ::isEntityBonus)
        answerAndValidate(items.last().name, R.id.et_list_answer, R.id.btn_list_validate, scrollable = false)
        waitFor { scenario.onMain { vm.listFinished.value } == true }
        assertUnlockedExactly(setOf("list_mythologie_shinto"), ignoring = ::isEntityBonus)
        pressBack()
        scenario.close()
    }

    // ── Entités nommées et collections (règles du ViewModel) ──────────────

    private fun newQuizViewModel(): QuizViewModel {
        var vm: QuizViewModel? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            vm = QuizViewModel(ApplicationProvider.getApplicationContext<Application>())
        }
        return vm!!
    }

    private fun QuizViewModel.callPrivate(method: String, argType: Class<*>, arg: Any?) {
        val m = QuizViewModel::class.java.getDeclaredMethod(method, argType).apply { isAccessible = true }
        InstrumentationRegistry.getInstrumentation().runOnMainSync { m.invoke(this, arg) }
    }

    @Test
    fun t08_namedEntitiesUnlockTheirAchievement() {
        val vm = newQuizViewModel()
        val expected = mapOf(
            "Heimdall" to "entity_heimdall",
            "Olorun" to "entity_olorun",
            "Bake Kujira" to "entity_bakekujira",
            "Ganesh" to "entity_ganesh",
            "Moritasgus" to "entity_moritasgus",
            "Arion" to "entity_arion",
            "Ulysse" to "entity_ulysse_telemaque",
            "Teutatès" to "entity_teutates",
            "Ptah" to "entity_ptah",
        )
        val all = runBlocking { AppDatabase.getInstance(context).entiteDao().getAllSync() }
        vm.callPrivate("checkNamedEntityAchievement", String::class.java, "Zeus")
        assertUnlockedExactly(emptySet())
        for ((name, id) in expected) {
            assertTrue("$name doit exister dans la base", all.any { it.name == name })
            assertNotNull("$id doit exister dans le catalogue", AchievementCatalog.byId(id))
            vm.callPrivate("checkNamedEntityAchievement", String::class.java, name)
            assertTrue("$name doit débloquer $id", AchievementManager.isUnlocked(id))
        }
        assertUnlockedExactly(expected.values.toSet())

        // Télémaque débloque le même succès qu'Ulysse.
        clearAchievements()
        vm.callPrivate("checkNamedEntityAchievement", String::class.java, "Télémaque")
        assertUnlockedExactly(setOf("entity_ulysse_telemaque"))
    }

    /** Toutes les entités de [targets] sauf la dernière : rien ; la dernière : [id]. */
    private fun checkCollection(vm: QuizViewModel, targets: List<EntiteEntity>, id: String) {
        assertTrue("$id : collection vide", targets.size >= 2)
        targets.dropLast(1).forEach { vm.callPrivate("checkCollectionAchievements", EntiteEntity::class.java, it) }
        Thread.sleep(1000) // vérification asynchrone (lecture de la base en coroutine)
        assertFalse("$id débloqué avant la dernière entité", AchievementManager.isUnlocked(id))
        vm.callPrivate("checkCollectionAchievements", EntiteEntity::class.java, targets.last())
        waitFor { AchievementManager.isUnlocked(id) }
    }

    @Test
    fun t09_collectionsUnlockOnceEveryEntityIsObtained() {
        val vm = newQuizViewModel()
        val all = runBlocking { AppDatabase.getInstance(context).entiteDao().getAllSync() }
        fun tags(e: EntiteEntity) = e.tags?.split(",")?.map { it.trim() }.orEmpty()

        checkCollection(vm, all.filter { it.monsterType == "Chien" }, "collection_dogs")
        checkCollection(vm, all.filter { it.race == "Zodiacal_Sign" && it.zodiacType == "Classique" }, "collection_zodiac")
        checkCollection(vm, all.filter { it.race == "Zodiacal_Sign" && it.zodiacType == "Chinois" }, "collection_zodiac_chinese")
        clearAchievements()
        checkCollection(vm, all.filter { "Animal" in tags(it) }, "collection_animals")
    }
}
