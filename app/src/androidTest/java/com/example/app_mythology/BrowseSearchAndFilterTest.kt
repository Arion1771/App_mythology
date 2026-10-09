package com.example.app_mythology

import android.widget.Spinner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_mythology.database.ArtifactEntity
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.database.PlaceEntity
import com.example.app_mythology.ui.MainActivity
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Contenu actuellement soumis à l'adapter de la liste [recyclerId]. */
@Suppress("UNCHECKED_CAST")
private fun <T> ActivityScenario<MainActivity>.listItems(recyclerId: Int): List<T> {
    var items = emptyList<T>()
    onActivity { activity ->
        val recycler = activity.findViewById<RecyclerView>(recyclerId)
        items = (recycler.adapter as ListAdapter<T, *>).currentList
    }
    return items
}

/** Libellés proposés par le spinner [spinnerId]. */
private fun ActivityScenario<MainActivity>.spinnerOptions(spinnerId: Int): List<String> {
    var options = emptyList<String>()
    onActivity { activity ->
        val adapter = activity.findViewById<Spinner>(spinnerId).adapter
        options = adapter?.let { a -> (0 until a.count).map { a.getItem(it).toString() } } ?: emptyList()
    }
    return options
}

/** Saisit [query] dans le champ de recherche intégré de la liste courante. */
private fun typeSearch(query: String) {
    onView(withId(androidx.appcompat.R.id.search_src_text)).perform(clearText(), typeText(query))
    closeSoftKeyboard()
}

private fun clearSearch() {
    onView(withId(androidx.appcompat.R.id.search_src_text)).perform(clearText())
    closeSoftKeyboard()
}

/**
 * Recherche et filtre par race des listes de consultation (Données →
 * Entités / Lieux / Artéfacts) — branche Test-Non-Regression.
 *
 * La recherche est un champ intégré en haut de chaque liste (elle avait
 * disparu avec la barre d'action en V4.3.1) : ces tests verrouillent sa
 * présence et son effet réel sur la liste affichée, ainsi que le filtre par
 * race du spinner de la liste des entités.
 */
@RunWith(AndroidJUnit4::class)
class BrowseSearchAndFilterTest {

    private fun openList(button: Int, destination: Int): ActivityScenario<MainActivity> {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.btn_primary_1)).perform(click()) // Home -> browseChoice
        onView(withId(button)).perform(click())
        scenario.assertCurrentDestination(destination)
        return scenario
    }

    // ── Entités ─────────────────────────────────────────────────────────────

    @Test
    fun entityListShowsSearchField() {
        val scenario = openList(R.id.btn_primary_1, R.id.entityListFragment)
        onView(withId(R.id.search_view)).check(matches(isDisplayed()))
        onView(withId(androidx.appcompat.R.id.search_src_text)).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun entitySearchFiltersByNameAndClearingRestoresFullList() {
        val scenario = openList(R.id.btn_primary_1, R.id.entityListFragment)
        waitFor { scenario.listItems<EntiteEntity>(R.id.recycler_entities).isNotEmpty() }
        val fullSize = scenario.listItems<EntiteEntity>(R.id.recycler_entities).size

        // Recherche partielle et insensible à la casse
        typeSearch("zeu")
        waitFor { scenario.listItems<EntiteEntity>(R.id.recycler_entities).size < fullSize }
        val results = scenario.listItems<EntiteEntity>(R.id.recycler_entities)
        assertTrue("Zeus doit apparaître dans les résultats", results.any { it.name == "Zeus" })
        assertTrue(
            "Chaque résultat doit contenir la saisie",
            results.all { it.name.contains("zeu", ignoreCase = true) }
        )

        // Aucune correspondance → liste vide
        typeSearch("xqzwvk")
        waitFor { scenario.listItems<EntiteEntity>(R.id.recycler_entities).isEmpty() }

        // Champ vidé → liste complète
        clearSearch()
        waitFor { scenario.listItems<EntiteEntity>(R.id.recycler_entities).size == fullSize }
        scenario.close()
    }

    @Test
    fun entityRaceFilterOffersEveryRace() {
        val scenario = openList(R.id.btn_primary_1, R.id.entityListFragment)
        onView(withId(R.id.spinner_filter)).check(matches(isDisplayed()))
        waitFor { scenario.listItems<EntiteEntity>(R.id.recycler_entities).isNotEmpty() }
        waitFor { scenario.spinnerOptions(R.id.spinner_filter).any { it.startsWith("Race : ") } }

        val raceOptions = scenario.spinnerOptions(R.id.spinner_filter).filter { it.startsWith("Race : ") }
        val distinctRaces = scenario.listItems<EntiteEntity>(R.id.recycler_entities).map { it.race }.toSet()
        assertEquals(
            "Une option « Race : … » par race présente en base",
            distinctRaces.size, raceOptions.size
        )
        scenario.close()
    }

    @Test
    fun entityRaceFilterShowsOnlyThatRaceAndResetRestoresAll() {
        val scenario = openList(R.id.btn_primary_1, R.id.entityListFragment)
        waitFor { scenario.listItems<EntiteEntity>(R.id.recycler_entities).isNotEmpty() }
        waitFor { scenario.spinnerOptions(R.id.spinner_filter).contains("Race : Titan") }
        val fullSize = scenario.listItems<EntiteEntity>(R.id.recycler_entities).size

        onView(withId(R.id.spinner_filter)).perform(click())
        onData(`is`("Race : Titan")).perform(click())
        waitFor { scenario.listItems<EntiteEntity>(R.id.recycler_entities).let { it.isNotEmpty() && it.size < fullSize } }
        val titans = scenario.listItems<EntiteEntity>(R.id.recycler_entities)
        assertTrue("Seuls des Titans doivent être listés", titans.all { it.race == "Titan" })
        assertTrue("Cronos fait partie des Titans", titans.any { it.name == "Cronos" })

        // Une race traduite (valeur en base anglaise, libellé français)
        onView(withId(R.id.spinner_filter)).perform(click())
        onData(`is`("Race : Dieu")).perform(click())
        waitFor { scenario.listItems<EntiteEntity>(R.id.recycler_entities).let { l -> l.isNotEmpty() && l.all { it.race == "God" } } }

        // Retour à l'option par défaut → liste complète
        onView(withId(R.id.spinner_filter)).perform(click())
        onData(`is`("Toutes (par race)")).perform(click())
        waitFor { scenario.listItems<EntiteEntity>(R.id.recycler_entities).size == fullSize }
        scenario.close()
    }

    // ── Artéfacts ───────────────────────────────────────────────────────────

    @Test
    fun artifactSearchFiltersByName() {
        val scenario = openList(R.id.btn_primary_3, R.id.artifactListFragment)
        onView(withId(R.id.search_view)).check(matches(isDisplayed()))
        waitFor { scenario.listItems<ArtifactEntity>(R.id.recycler_entities).isNotEmpty() }
        val fullSize = scenario.listItems<ArtifactEntity>(R.id.recycler_entities).size

        typeSearch("excal")
        waitFor { scenario.listItems<ArtifactEntity>(R.id.recycler_entities).size < fullSize }
        val results = scenario.listItems<ArtifactEntity>(R.id.recycler_entities)
        assertTrue(results.any { it.name == "Excalibur" })
        assertTrue(results.all { it.name.contains("excal", ignoreCase = true) })

        clearSearch()
        waitFor { scenario.listItems<ArtifactEntity>(R.id.recycler_entities).size == fullSize }
        scenario.close()
    }

    // ── Lieux ───────────────────────────────────────────────────────────────

    @Test
    fun placeSearchFiltersByNameAndTakesPrecedenceOverFilter() {
        val scenario = openList(R.id.btn_primary_2, R.id.placeListFragment)
        onView(withId(R.id.search_view)).check(matches(isDisplayed()))
        waitFor { scenario.listItems<PlaceEntity>(R.id.recycler_places).isNotEmpty() }
        val fullSize = scenario.listItems<PlaceEntity>(R.id.recycler_places).size

        typeSearch("styx")
        waitFor { scenario.listItems<PlaceEntity>(R.id.recycler_places).size < fullSize }
        val results = scenario.listItems<PlaceEntity>(R.id.recycler_places)
        assertTrue(results.any { it.name == "Styx" })
        assertTrue(results.all { it.name.contains("styx", ignoreCase = true) })

        // Changer le filtre pendant une recherche ne doit pas l'écraser
        onView(withId(R.id.spinner_place_filter)).perform(click())
        onData(`is`("Royaumes")).perform(click())
        Thread.sleep(500)
        assertEquals(results, scenario.listItems<PlaceEntity>(R.id.recycler_places))

        // Champ vidé → le filtre sélectionné (Royaumes) reprend la main
        clearSearch()
        waitFor {
            scenario.listItems<PlaceEntity>(R.id.recycler_places)
                .let { l -> l.isNotEmpty() && l.all { it.placeType == "Royaume" } }
        }
        scenario.close()
    }
}
