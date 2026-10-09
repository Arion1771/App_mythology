package com.example.app_mythology.quiz

import com.example.app_mythology.database.ArtifactEntity
import com.example.app_mythology.database.EntiteEntity
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.io.File

/**
 * Vérifie que chaque thème du mode Liste (`ListThemeCatalog`/`resolveGroups`)
 * prend bien en compte **toutes** les entités/artéfacts de `prepopulate.json`
 * qui lui correspondent, ni plus ni moins (branche Test-Non-Regression).
 *
 * Pour les thèmes déductibles d'un champ (mythologie, race, godType,
 * museType, zodiacType), l'ensemble attendu est recalculé indépendamment à
 * partir du JSON brut (pas via `EntiteEntity`/`ListThemeCatalog`), pour que
 * le test détecte aussi bien un bug de `ListThemeCatalog` qu'une valeur de
 * champ incohérente dans les données (c'est exactement ce qui serait passé
 * inaperçu si Base.md affichait un total périmé, comme « Dieux : 225 » après
 * l'ajout de 4 nouveaux Dieux portant le vrai total à 229).
 *
 * Pour les 7 thèmes curés (champ `listThemes`), l'ensemble attendu est
 * directement le filtrage de ce champ : la correspondance avec le contenu
 * résolu est alors de toute façon garantie par construction côté code
 * (`byListTheme`), donc le test porte surtout sur la cohérence des valeurs
 * de `listThemes` elles-mêmes (vocabulaire fermé, déjà verrouillé par
 * ailleurs dans `PrepopulateDataRegressionTest`).
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ListThemeCompletenessTest {

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
        val ENTITES_JSON: JSONArray = ROOT.getJSONArray("entites")
        val ARTIFACTS_JSON: JSONArray = ROOT.getJSONArray("artifacts")

        fun JSONArray.objects(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }
        fun JSONObject.strOrNull(key: String): String? =
            if (has(key) && !isNull(key)) getString(key) else null

        fun listThemesOf(o: JSONObject): List<String> =
            o.strOrNull("listThemes")?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()

        /** Reproduit le mapping JSON -> EntiteEntity de AppDatabase.entiteFromJson (champs utiles aux thèmes). */
        fun entiteFromJson(o: JSONObject) = EntiteEntity(
            name = o.getString("name"),
            mythology = o.getString("mythology"),
            race = o.getString("race"),
            difficulty = if (o.has("difficulty") && !o.isNull("difficulty")) o.getInt("difficulty") else 1,
            godType = o.strOrNull("godType"),
            monsterType = o.strOrNull("monsterType"),
            museType = o.strOrNull("museType"),
            zodiacType = o.strOrNull("zodiacType"),
            tags = o.strOrNull("tags"),
            listThemes = o.strOrNull("listThemes"),
        )

        fun artifactFromJson(o: JSONObject) = ArtifactEntity(
            name = o.getString("name"),
            mythology = o.getString("mythology"),
            artifactType = o.getString("artifactType"),
            difficulty = if (o.has("difficulty") && !o.isNull("difficulty")) o.getInt("difficulty") else 1,
            tags = o.strOrNull("tags"),
        )

        val ENTITES: List<EntiteEntity> = ENTITES_JSON.objects().map(::entiteFromJson)
        val ARTIFACTS: List<ArtifactEntity> = ARTIFACTS_JSON.objects().map(::artifactFromJson)
    }

    /**
     * Noms résolus par le thème réel (`ListThemeCatalog`), toutes sous-sections
     * confondues, triés mais **sans déduplication** (multiset) : plusieurs
     * entités en base partagent le même nom (ex. les deux « Morrigan »,
     * irlandaise et gauloise, toutes deux de race God), un simple `Set`
     * masquerait la perte d'un doublon.
     */
    private fun resolvedNames(themeId: String): List<String> {
        val theme = ListThemeCatalog.byId(themeId) ?: error("Thème inconnu : $themeId")
        return theme.resolveGroups(ENTITES, ARTIFACTS).flatMap { it.items }.map { it.name }.sorted()
    }

    /** Noms attendus (multiset trié) d'après une relecture indépendante du JSON brut. */
    private fun expectedEntityNames(predicate: (JSONObject) -> Boolean): List<String> =
        ENTITES_JSON.objects().filter(predicate).map { it.getString("name") }.sorted()

    private fun assertThemeComplete(themeId: String, expected: List<String>) {
        val actual = resolvedNames(themeId)
        val missing = (expected - actual.toSet())
        val extra = (actual - expected.toSet())
        assertEquals(
            "Thème '$themeId' incomplet ou incohérent (tailles : attendu ${expected.size}, résolu ${actual.size}) — " +
                "manquantes : $missing, en trop : $extra",
            expected, actual,
        )
    }

    // ── Thèmes déductibles d'un champ ──────────────────────────────────────

    @Test fun t01_mythologieGrecque() = assertThemeComplete("mythologie_grecque", expectedEntityNames { it.getString("mythology") == "Grecque" })
    @Test fun t02_mythologieRomaine() = assertThemeComplete("mythologie_romaine", expectedEntityNames { it.getString("mythology") == "Romaine" })
    @Test fun t03_mythologieHindoue() = assertThemeComplete("mythologie_hindoue", expectedEntityNames { it.getString("mythology") == "Hindouisme" })
    @Test fun t04_mythologieChinoise() = assertThemeComplete("mythologie_chinoise", expectedEntityNames { it.getString("mythology") == "Chinoise" })
    @Test fun t05_mythologieShinto() = assertThemeComplete("mythologie_shinto", expectedEntityNames { it.getString("mythology") == "Shinto" })
    @Test fun t06_mythologieAmeriqueDuSud() = assertThemeComplete(
        "mythologie_amerique_sud",
        expectedEntityNames { it.getString("mythology") in setOf("Maya", "Aztèque") },
    )

    @Test fun t07_dieux() = assertThemeComplete("dieux", expectedEntityNames { it.getString("race") == "God" })
    @Test fun t08_monstres() = assertThemeComplete("monstres", expectedEntityNames { it.getString("race") == "Monster" })
    @Test fun t09_heros() = assertThemeComplete("heros", expectedEntityNames { it.getString("race") == "Heroes" })
    @Test fun t10_grees() = assertThemeComplete("grees", expectedEntityNames { it.getString("race") == "Grées" })
    @Test fun t11_erinyes() = assertThemeComplete("erinyes", expectedEntityNames { it.getString("race") == "Erinyes" })

    @Test fun t12_entites() = assertThemeComplete("entites", ENTITES_JSON.objects().map { it.getString("name") }.sorted())

    @Test fun t13_artefacts() {
        val expected = ARTIFACTS_JSON.objects().map { it.getString("name") }.sorted()
        val actual = ListThemeCatalog.byId("artefacts")!!.resolveGroups(ENTITES, ARTIFACTS)
            .flatMap { it.items }.map { it.name }.sorted()
        assertEquals(expected, actual)
    }

    @Test fun t14_muses() = assertThemeComplete(
        "muses",
        expectedEntityNames { o ->
            (o.strOrNull("museType") == "Grecque" && o.getString("mythology") == "Grecque") ||
                o.strOrNull("museType") == "Beotienne"
        },
    )

    @Test fun t15_olympiensGrecs() = assertThemeComplete(
        "olympiens_grecs",
        expectedEntityNames { it.strOrNull("godType") == "Olympien" && it.getString("mythology") == "Grecque" },
    )

    @Test fun t16_olympiensRomains() = assertThemeComplete(
        "olympiens_romains",
        expectedEntityNames { it.strOrNull("godType") == "Olympien" && it.getString("mythology") == "Romaine" },
    )

    @Test fun t17_geantsGrecs() = assertThemeComplete(
        "geants_grecs",
        expectedEntityNames { it.getString("race") == "Giant" && it.getString("mythology") == "Grecque" },
    )

    @Test fun t18_enfantsGaiaOuranos() = assertThemeComplete(
        "enfants_gaia_ouranos",
        expectedEntityNames { it.getString("mythology") == "Grecque" && it.getString("race") in setOf("Titan", "Cyclope", "Hecatoncheires") },
    )

    @Test fun t19_archangesEtDemons() = assertThemeComplete(
        "archanges_demons",
        expectedEntityNames { it.getString("race") in setOf("Archangels", "Demon_Prince") },
    )

    @Test fun t20_signesDuZodiaque() = assertThemeComplete(
        "zodiaque",
        expectedEntityNames { it.strOrNull("zodiacType") in setOf("Classique", "Chinois") },
    )

    // ── Thèmes curés (champ listThemes) ────────────────────────────────────

    private val curatedThemeTitles = mapOf(
        "monstres_12_travaux" to "Monstres des 12 travaux",
        "guerriers_troie" to "Guerriers grecs devant Troie",
        "yokais" to "Yokais",
        "argonautes" to "Argonautes",
        "chevaliers_table_ronde" to "Chevaliers de la table ronde",
        "grands_dieux_egypte" to "Grands dieux d'Égypte",
        "monstres_arbre_monde" to "Monstres de l'arbre monde",
    )

    @Test
    fun t21_everyCuratedThemeResolvesExactlyItsListThemesMembers() {
        for ((themeId, title) in curatedThemeTitles) {
            val expected = expectedEntityNames { title in listThemesOf(it) }
            assertThemeComplete(themeId, expected)
        }
    }

}
