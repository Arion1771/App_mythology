package com.example.app_mythology.nonregression

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Tests de non-régression sur `app/src/main/assets/prepopulate.json` (branche
 * Test-Non-Regression).
 *
 * Ils ne testent pas du code mais **verrouillent les invariants de la base**
 * et re-vérifient des bugs déjà corrigés dans l'historique, pour empêcher
 * qu'une future édition de données les réintroduise :
 *  - coquille « Primodrial » (V1.6.3 / V1.6.4)
 *  - patronyme « Pendragon » retiré d'Arthur et Uther (V3.2.4)
 *  - articles retirés en tête de nom (V2.0.6)
 *  - 7 archanges originels seulement (V2.3.2)
 *  - descriptions distinctes de l'indice de quiz (V2.0.6 / V2.0.7)
 *  - vocabulaire fermé du champ listThemes (V3.0.0 + règle CLAUDE.md)
 *  - Orthos de type de monstre « Chien » (V3.4.2)
 *
 * Exécutables via `./gradlew :app:testDebugUnitTest` (répertoire de travail =
 * module `app/`).
 */
class PrepopulateDataRegressionTest {

    private companion object {
        val RAW: String = run {
            val candidates = listOf(
                "src/main/assets/prepopulate.json",
                "app/src/main/assets/prepopulate.json",
            )
            candidates.map(::File).firstOrNull { it.exists() }
                ?.readText(Charsets.UTF_8)
                ?: error("prepopulate.json introuvable (cwd=${File(".").absolutePath})")
        }
        val ROOT: JSONObject = JSONObject(RAW)
        val ENTITES: JSONArray = ROOT.getJSONArray("entites")
        val PLACES: JSONArray = ROOT.getJSONArray("places")
        val ARTIFACTS: JSONArray = ROOT.getJSONArray("artifacts")

        val CURATED_LIST_THEMES = setOf(
            "Guerriers grecs devant Troie",
            "Argonautes",
            "Chevaliers de la table ronde",
            "Grands dieux d'Égypte",
            "Monstres de l'arbre monde",
            "Monstres des 12 travaux",
            "Yokais",
        )
        val KNOWN_RACES = setOf(
            "Archangels", "Arthurian_Knight", "Cyclope", "Demon_Prince", "Erinyes",
            "Giant", "God", "Grées", "Hecatoncheires", "Heroes", "Monster", "Muses",
            "Titan", "Valkyrie", "Zodiacal_Sign",
        )
        val KNOWN_ARTIFACT_TYPES = setOf("Arme", "Artefact", "Objet magique", "Véhicule", "Nourriture")

        fun JSONArray.objects(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }
        fun JSONObject.strOrNull(key: String): String? =
            if (has(key) && !isNull(key)) getString(key) else null
        fun JSONObject.listThemes(): List<String> =
            strOrNull("listThemes")?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    }

    // ── Structure ──────────────────────────────────────────────────────────

    @Test
    fun `root holds the three non-empty collections`() {
        assertTrue(ENTITES.length() > 400)
        assertTrue(ARTIFACTS.length() > 30)
        assertTrue(PLACES.length() > 15)
    }

    @Test
    fun `entity ids present are unique, and place ids are unique`() {
        val entityIds = ENTITES.objects().filter { it.has("id") }.map { it.getInt("id") }
        assertEquals("ids d'entités dupliqués", entityIds.size, entityIds.toSet().size)

        val placeIds = PLACES.objects().map { it.getInt("id") }
        assertEquals("ids de lieux dupliqués", placeIds.size, placeIds.toSet().size)
    }

    // ── Champs obligatoires / plages de valeurs ────────────────────────────

    @Test
    fun `every entity has the mandatory quiz fields and a difficulty within 1 to 3`() {
        for (e in ENTITES.objects()) {
            val name = e.strOrNull("name")
            assertTrue("nom d'entité vide", !name.isNullOrBlank())
            assertTrue("$name : mythologie vide", !e.strOrNull("mythology").isNullOrBlank())
            assertTrue("$name : race vide", !e.strOrNull("race").isNullOrBlank())
            assertTrue("$name : indice de quiz vide", !e.strOrNull("clue").isNullOrBlank())
            val d = e.getInt("difficulty")
            assertTrue("$name : difficulté $d hors de 1..3", d in 1..3)
        }
    }

    @Test
    fun `every artifact has mandatory fields, a known type and a difficulty within 1 to 3`() {
        for (a in ARTIFACTS.objects()) {
            val name = a.strOrNull("name")
            assertTrue("nom d'artéfact vide", !name.isNullOrBlank())
            assertTrue("$name : mythologie vide", !a.strOrNull("mythology").isNullOrBlank())
            assertTrue("$name : indice de quiz vide", !a.strOrNull("clue").isNullOrBlank())
            val type = a.strOrNull("artifactType")
            assertTrue("$name : type d'artéfact inconnu ($type)", type in KNOWN_ARTIFACT_TYPES)
            assertTrue("$name : difficulté hors de 1..3", a.getInt("difficulty") in 1..3)
        }
    }

    @Test
    fun `every place has the mandatory fields`() {
        for (p in PLACES.objects()) {
            assertTrue(!p.strOrNull("name").isNullOrBlank())
            assertTrue(!p.strOrNull("mythology").isNullOrBlank())
            assertTrue(!p.strOrNull("description").isNullOrBlank())
            assertTrue(!p.strOrNull("placeType").isNullOrBlank())
        }
    }

    @Test
    fun `every entity race belongs to the known set`() {
        val unknown = ENTITES.objects().map { it.getString("race") }.toSet() - KNOWN_RACES
        assertTrue("races inconnues : $unknown", unknown.isEmpty())
    }

    // ── Bugs déjà corrigés — ne doivent pas revenir ────────────────────────

    @Test
    fun `the Primodrial typo never reappears`() {
        assertFalse("coquille 'Primodrial' réintroduite (cf. V1.6.4)", RAW.contains("Primodrial"))
    }

    @Test
    fun `the Pendragon surname stays removed from names`() {
        val offenders = ENTITES.objects().map { it.getString("name") }.filter { it.contains("Pendragon") }
        assertTrue("« Pendragon » réintroduit dans un nom (cf. V3.2.4) : $offenders", offenders.isEmpty())
    }

    @Test
    fun `no entity name starts with a French article`() {
        val articles = Regex("^(le|la|les|l'|un|une|des)\\s|^l'", RegexOption.IGNORE_CASE)
        val offenders = ENTITES.objects().map { it.getString("name") }
            .filter { articles.containsMatchIn(it) }
        assertTrue("noms commençant par un article (cf. V2.0.6) : $offenders", offenders.isEmpty())
    }

    @Test
    fun `entity descriptions are never identical to the quiz clue`() {
        val offenders = ENTITES.objects()
            .filter { it.strOrNull("description") != null && it.strOrNull("description") == it.strOrNull("clue") }
            .map { it.getString("name") }
        assertTrue("description = indice (cf. V2.0.6 / V2.0.7) : $offenders", offenders.isEmpty())
    }

    @Test
    fun `only the seven canonical archangels remain`() {
        val archangels = ENTITES.objects().filter { it.getString("race") == "Archangels" }.map { it.getString("name") }
        assertEquals("le nombre d'archanges doit rester à 7 (cf. V2.3.2) : $archangels", 7, archangels.size)

        val forbidden = setOf("Chamuel", "Haniel", "Jophiel", "Métatron", "Metatron", "Sandalphon", "Zadkiel")
        assertTrue(
            "archange de tradition ésotérique réintroduit : ${archangels.filter { it in forbidden }}",
            archangels.none { it in forbidden },
        )
    }

    // ── Cohérence des thèmes du mode Liste ─────────────────────────────────

    @Test
    fun `listThemes only ever references the curated vocabulary`() {
        val used = ENTITES.objects().flatMap { it.listThemes() }.toSet()
        val unknown = used - CURATED_LIST_THEMES
        assertTrue("valeurs de listThemes hors vocabulaire curé : $unknown", unknown.isEmpty())
    }

    @Test
    fun `every curated list theme has at least one member`() {
        val used = ENTITES.objects().flatMap { it.listThemes() }.toSet()
        val missing = CURATED_LIST_THEMES - used
        assertTrue("thèmes curés sans aucune entité : $missing", missing.isEmpty())
    }

    // ── Verrous data ponctuels ────────────────────────────────────────────

    @Test
    fun `Orthos is a Chien monster`() {
        val orthos = ENTITES.objects().single { it.getString("name") == "Orthos" }
        assertEquals("Chien", orthos.strOrNull("monsterType"))
    }

    @Test
    fun `Grecque stays the most represented mythology`() {
        val top = ENTITES.objects().map { it.getString("mythology") }
            .groupingBy { it }.eachCount()
            .maxByOrNull { it.value }!!.key
        assertEquals("Grecque", top)
    }
}
