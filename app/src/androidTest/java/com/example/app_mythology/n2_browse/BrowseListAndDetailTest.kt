package com.example.app_mythology.n2_browse

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.R
import com.example.app_mythology.assertCurrentDestination
import com.example.app_mythology.currentScreenView
import com.example.app_mythology.database.AppDatabase
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.onMain
import com.example.app_mythology.ui.MainActivity
import com.example.app_mythology.waitFor
import com.example.app_mythology.waitForDestination
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.sameInstance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

private val db get() = AppDatabase.getInstance(ApplicationProvider.getApplicationContext())

/** Contenu actuellement soumis à l'adapter de la liste [recyclerId]. */
private fun ActivityScenario<MainActivity>.listItems(recyclerId: Int): List<Any> = onMain {
    (it.findViewById<RecyclerView>(recyclerId).adapter as ListAdapter<*, *>).currentList.toList()
}

/** Libellés français attendus sur la fiche (même traduction que l'écran de détail). */
private fun expectedRace(race: String) = when (race) {
    "God" -> "Dieu"; "Giant" -> "Géant"; "Heroes" -> "Héros"; "Monster" -> "Monstre"
    "Hecatoncheires" -> "Hécatonchire"; "Muses" -> "Muse"; "Erinyes" -> "Érinye"; "Grées" -> "Grée"
    "Archangels" -> "Archange"; "Arthurian_Knight" -> "Chevalier Arthurien"; "Demon_Prince" -> "Démon"
    "Zodiacal_Sign" -> "Signe du Zodiaque"
    else -> race
}

private fun expectedDifficulty(d: Int) = when (d) { 1 -> "Facile"; 2 -> "Moyen"; 3 -> "Difficile"; else -> d.toString() }

/** Une ligne de la fiche : son conteneur, son texte, et la valeur attendue (null = ligne masquée). */
private class DetailRow(val label: String, val rowId: Int, val textId: Int, val value: (EntiteEntity) -> String?)

private val DETAIL_ROWS = listOf(
    DetailRow("domaine", R.id.row_domain, R.id.tv_detail_domain) { it.domain },
    DetailRow("type divin", R.id.row_godtype, R.id.tv_detail_godtype) {
        it.godType?.let { t -> if (t == "Primodrial") "Primordial" else t }
    },
    DetailRow("équivalent", R.id.row_equivalent, R.id.tv_detail_equivalent) { it.equivalentName },
    DetailRow("père", R.id.row_father, R.id.tv_detail_father) { it.fatherName },
    DetailRow("mère", R.id.row_mother, R.id.tv_detail_mother) { it.motherName },
    DetailRow("type de géant", R.id.row_gianttype, R.id.tv_detail_gianttype) { it.giantType },
    DetailRow("histoire", R.id.row_story, R.id.tv_detail_story) { it.story },
    DetailRow("tué par", R.id.row_killer, R.id.tv_detail_killer) { it.killer },
    DetailRow("ascendant", R.id.row_ascendant, R.id.tv_detail_ascendant) { it.ascendantName },
    DetailRow("type de monstre", R.id.row_monstertype, R.id.tv_detail_monstertype) { it.monsterType },
    DetailRow("description", R.id.row_description, R.id.tv_detail_description) { it.description },
    DetailRow("primordial", R.id.row_primordial, R.id.tv_detail_primordial) {
        it.primordial?.let { p -> if (p) "Oui" else "Non" }
    },
    DetailRow("type de muse", R.id.row_musetype, R.id.tv_detail_musetype) { it.museType },
    DetailRow("zodiaque", R.id.row_zodiac, R.id.tv_detail_zodiac) { it.zodiacType },
    DetailRow("rôle", R.id.row_role, R.id.tv_detail_role) { it.role },
    DetailRow("mort", R.id.row_death, R.id.tv_detail_death) { it.death },
    DetailRow("culture populaire", R.id.row_popularculture, R.id.tv_detail_popularculture) { it.popularCulture },
)

/**
 * Listes de consultation et fiche détaillée d'une entité — branche
 * Test-Non-Regression : chaque liste contient toutes les lignes de la base,
 * défile au doigt et jusqu'au dernier élément ; un clic sur une entité ouvre
 * sa fiche, qui affiche toutes ses données (chaque ligne visible avec la bonne
 * valeur si le champ est renseigné, masquée sinon), atteignables en faisant
 * défiler la fiche ; le lien « équivalent » ouvre la fiche correspondante, et
 * le retour ramène alors à la liste.
 *
 * Restent volontairement hors fiche : l'indice (propre au quiz), les tags
 * (leurres du QCM) et les thèmes du mode Liste, données internes.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class BrowseListAndDetailTest {

    private fun openList(button: Int, destination: Int): ActivityScenario<MainActivity> {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_1)).perform(click()) // Données
        onView(withId(button)).perform(click())
        scenario.assertCurrentDestination(destination)
        return scenario
    }

    /** Toute la liste est chargée, défile au doigt, et son dernier élément est atteignable et affiché. */
    private fun listScrollsThroughEveryItem(
        button: Int, destination: Int, recyclerId: Int, dbCount: Int, nameOf: (Any) -> String,
    ) {
        val scenario = openList(button, destination)
        waitFor { scenario.listItems(recyclerId).size == dbCount }

        fun firstVisible() = scenario.onMain {
            (it.findViewById<RecyclerView>(recyclerId).layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        }
        assertEquals("La liste démarre en haut", 0, firstVisible())
        onView(withId(recyclerId)).perform(swipeUp())
        waitFor { firstVisible() > 0 }

        val last = dbCount - 1
        scenario.onActivity { it.findViewById<RecyclerView>(recyclerId).scrollToPosition(last) }
        waitFor {
            scenario.onMain { it.findViewById<RecyclerView>(recyclerId).findViewHolderForAdapterPosition(last) != null }
        }
        val shown = scenario.onMain {
            it.findViewById<RecyclerView>(recyclerId).findViewHolderForAdapterPosition(last)!!
                .itemView.findViewById<TextView>(R.id.tv_entity_name).text.toString()
        }
        assertEquals("Dernier élément affiché", nameOf(scenario.listItems(recyclerId).last()), shown)

        scenario.onActivity { it.findViewById<RecyclerView>(recyclerId).scrollToPosition(0) }
        waitFor { firstVisible() == 0 }
        scenario.close()
    }

    @Test
    fun t01_entityListScrollsThroughEveryEntity() = listScrollsThroughEveryItem(
        R.id.btn_primary_1, R.id.entityListFragment, R.id.recycler_entities,
        runBlocking { db.entiteDao().getAllSync().size }
    ) { (it as EntiteEntity).name }

    @Test
    fun t02_placeListScrollsThroughEveryPlace() = listScrollsThroughEveryItem(
        R.id.btn_primary_2, R.id.placeListFragment, R.id.recycler_places,
        runBlocking { db.placeDao().getAllSync().size }
    ) { (it as com.example.app_mythology.database.PlaceEntity).name }

    @Test
    fun t03_artifactListScrollsThroughEveryArtifact() = listScrollsThroughEveryItem(
        R.id.btn_primary_3, R.id.artifactListFragment, R.id.recycler_entities,
        runBlocking { db.artifactDao().getAllSync().size }
    ) { (it as com.example.app_mythology.database.ArtifactEntity).name }

    /** Recherche [entity] dans la liste des entités, clique dessus et attend l'ouverture de sa fiche. */
    private fun openDetailFromList(scenario: ActivityScenario<MainActivity>, entity: EntiteEntity) {
        val all = runBlocking { db.entiteDao().getAllSync() }
        // Le filtre se reconstruit deux fois à chaque (re)création de la liste (races, puis
        // mythologies), et chaque reconstruction resélectionne « Toutes », ce qui réaffiche
        // toute la base : on attend la version finale du filtre et la liste complète.
        val filterEntries = 1 + all.map { it.race }.distinct().size + all.map { it.mythology }.distinct().size
        waitFor(timeoutMs = 10_000) {
            scenario.onMain { it.findViewById<android.widget.Spinner>(R.id.spinner_filter).adapter?.count } == filterEntries &&
                scenario.listItems(R.id.recycler_entities).size == all.size
        }
        androidx.test.espresso.Espresso.onIdle()

        // Quand la liste passe de toute la base aux résultats, ses lignes glissent de leur
        // ancienne position vers la nouvelle (animation qu'Espresso n'attend pas) : la
        // ligne visée était tapée en bas d'écran, à moitié coupée. Animation coupée sur la
        // liste de ce test uniquement (l'application n'est pas modifiée).
        scenario.onActivity { it.findViewById<RecyclerView>(R.id.recycler_entities).itemAnimator = null }

        fun searchResultsOnly(): Boolean {
            val items = scenario.listItems(R.id.recycler_entities).map { it as EntiteEntity }
            return items.size < all.size && items.all { it.name.contains(entity.name, ignoreCase = true) } &&
                items.any { it.id == entity.id }
        }

        // Par sécurité, la recherche est refaite si la liste a encore changé sous le test.
        for (attempt in 1..3) {
            onView(withId(androidx.appcompat.R.id.search_src_text)).perform(replaceText(entity.name))
            closeSoftKeyboard()
            waitFor { searchResultsOnly() }
            val position = scenario.listItems(R.id.recycler_entities).indexOfFirst { (it as EntiteEntity).id == entity.id }

            // Élément amené en haut de la liste, entièrement visible (Espresso refuse de taper
            // une vue visible à moins de 90 %) ; défilement demandé puis attente hors de
            // l'application, qui met la liste en page librement.
            scenario.onActivity {
                (it.findViewById<RecyclerView>(R.id.recycler_entities).layoutManager as LinearLayoutManager)
                    .scrollToPositionWithOffset(position, 0)
            }
            var item: View? = null
            waitFor {
                item = scenario.onMain {
                    val recycler = it.findViewById<RecyclerView>(R.id.recycler_entities)
                    // Ligne retenue seulement une fois la liste immobile et la ligne entièrement dedans.
                    recycler.findViewHolderForAdapterPosition(position)?.itemView?.takeIf { row ->
                        !recycler.isAnimating && row.top >= 0 && row.bottom <= recycler.height
                    }
                }
                item != null
            }
            androidx.test.espresso.Espresso.onIdle()

            // Juste avant le tap : la liste ne contient toujours que les résultats et la
            // ligne visée affiche bien le nom de l'entité ; sinon on recommence.
            val rowName = scenario.onMain { item!!.findViewById<TextView>(R.id.tv_entity_name).text.toString() }
            if (!searchResultsOnly() || rowName != entity.name) continue

            onView(sameInstance<View>(item!!)).perform(click())
            scenario.waitForDestination(R.id.entityDetailFragment)
            waitFor {
                scenario.currentScreenView<TextView>(R.id.tv_detail_name)?.text?.toString() == entity.name
            }
            return
        }
        throw AssertionError("Impossible d'ouvrir la fiche de ${entity.name} depuis la liste après 3 recherches")
    }

    /** Vérifie chaque donnée de la fiche de [e], en faisant défiler la fiche jusqu'à chaque ligne affichée. */
    private fun assertDetailShowsEverything(scenario: ActivityScenario<MainActivity>, e: EntiteEntity) {
        // Après une navigation fiche -> fiche, l'ancienne fiche reste un instant dans la
        // fenêtre : on attend qu'il n'en reste qu'une avant les vérifications Espresso.
        fun countNames(v: View): Int = (if (v.id == R.id.tv_detail_name) 1 else 0) +
            ((v as? android.view.ViewGroup)?.let { g -> (0 until g.childCount).sumOf { countNames(g.getChildAt(it)) } } ?: 0)
        waitFor { scenario.onMain { countNames(it.window.decorView) } == 1 }

        onView(withId(R.id.tv_detail_name)).check(matches(withText(e.name)))
        onView(withId(R.id.tv_detail_mythology)).check(matches(withText("Mythologie : ${e.mythology}")))
        onView(withId(R.id.tv_detail_race)).check(matches(withText("Race : ${expectedRace(e.race)}")))
        onView(withId(R.id.tv_detail_difficulty)).check(matches(withText("Niveau : ${expectedDifficulty(e.difficulty)}")))

        for (row in DETAIL_ROWS) {
            val expected = row.value(e)?.takeIf { it.isNotBlank() }
            val visible = scenario.currentScreenView<View>(row.rowId)?.visibility == View.VISIBLE
            if (expected == null) {
                assertTrue("« ${row.label} » de ${e.name} : ligne attendue masquée", !visible)
            } else {
                assertTrue("« ${row.label} » de ${e.name} : ligne attendue affichée", visible)
                onView(withId(row.rowId)).perform(scrollTo()).check(matches(isDisplayed()))
                onView(withId(row.textId)).check(matches(withText(expected)))
            }
        }
    }

    @Test
    fun t04_clickingAnEntityShowsAllItsData() {
        val all = runBlocking { db.entiteDao().getAllSync() }
        // Zeus (fiche très fournie) + au moins une entité par ligne de la fiche, pour que
        // chaque donnée affichable soit vérifiée au moins une fois. Une ligne renseignée
        // pour aucune entité de la base serait vérifiée masquée sur chaque fiche.
        val sample = LinkedHashSet<EntiteEntity>()
        all.firstOrNull { it.name == "Zeus" }?.let { sample += it }
        for (row in DETAIL_ROWS) {
            if (sample.none { !row.value(it).isNullOrBlank() }) {
                all.firstOrNull { !row.value(it).isNullOrBlank() }?.let { sample += it }
            }
        }

        val scenario = openList(R.id.btn_primary_1, R.id.entityListFragment)
        for (entity in sample) {
            openDetailFromList(scenario, entity)
            assertDetailShowsEverything(scenario, entity)
            pressBack()
            scenario.waitForDestination(R.id.entityListFragment)
        }
        scenario.close()
    }

    @Test
    fun t05_equivalentLinkOpensTheEquivalentEntity() {
        val all = runBlocking { db.entiteDao().getAllSync() }
        val entity = all.first { e -> !e.equivalentName.isNullOrBlank() && all.any { it.name == e.equivalentName } }
        val equivalent = all.first { it.name == entity.equivalentName }

        val scenario = openList(R.id.btn_primary_1, R.id.entityListFragment)
        openDetailFromList(scenario, entity)
        onView(withId(R.id.tv_detail_equivalent)).perform(scrollTo(), click())
        waitFor {
            scenario.currentScreenView<TextView>(R.id.tv_detail_name)?.text?.toString() == equivalent.name
        }
        scenario.assertCurrentDestination(R.id.entityDetailFragment)
        assertDetailShowsEverything(scenario, equivalent)

        // L'action « équivalent » remplace la fiche d'origine (popUpTo de la liste, pour ne
        // pas empiler les fiches d'équivalent en équivalent) : le retour ramène à la liste.
        pressBack()
        scenario.waitForDestination(R.id.entityListFragment)
        scenario.close()
    }
}
