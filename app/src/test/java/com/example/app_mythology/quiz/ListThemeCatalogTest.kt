package com.example.app_mythology.quiz

import com.example.app_mythology.database.ArtifactEntity
import com.example.app_mythology.database.EntiteEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests unitaires de [ListThemeCatalog] : intégrité du catalogue des thèmes du
 * quiz Liste et comportement déterministe des fonctions de regroupement
 * (filtrage par mythologie/race, sous-groupes, ordre canonique des mythologies).
 */
class ListThemeCatalogTest {

    private fun ent(
        id: Int, name: String, mythology: String, race: String,
        godType: String? = null, museType: String? = null, zodiacType: String? = null,
    ) = EntiteEntity(
        id = id, name = name, mythology = mythology, race = race,
        godType = godType, museType = museType, zodiacType = zodiacType,
    )

    private fun art(id: Int, name: String) =
        ArtifactEntity(id = id, name = name, mythology = "Nordique", artifactType = "Arme")

    private fun groups(themeId: String, entites: List<EntiteEntity> = emptyList(), artifacts: List<ArtifactEntity> = emptyList()) =
        ListThemeCatalog.byId(themeId)!!.resolveGroups(entites, artifacts)

    // ── Intégrité du catalogue ─────────────────────────────────────────────

    @Test
    fun `catalog exposes 27 themes with unique ids and non-blank titles`() {
        val all = ListThemeCatalog.all
        assertEquals(27, all.size)
        assertEquals("les identifiants de thème doivent être uniques", all.size, all.map { it.id }.toSet().size)
        assertTrue(all.all { it.title.isNotBlank() })
    }

    @Test
    fun `byId round-trips for every theme and rejects unknown ids`() {
        for (theme in ListThemeCatalog.all) {
            assertEquals(theme, ListThemeCatalog.byId(theme.id))
        }
        assertNull(ListThemeCatalog.byId("theme_inexistant"))
    }

    @Test
    fun `only the artefacts theme is an ArtifactTheme`() {
        val artifactThemes = ListThemeCatalog.all.filterIsInstance<ListTheme.ArtifactTheme>()
        assertEquals(1, artifactThemes.size)
        assertEquals("artefacts", artifactThemes.single().id)
        assertTrue(ListThemeCatalog.all.filter { it.id != "artefacts" }.all { it is ListTheme.EntityTheme })
    }

    @Test
    fun `every theme uses one of the three attempt tiers`() {
        assertTrue(ListThemeCatalog.all.all { it.maxErrors in setOf(3, 5, 10) })
    }

    // ── Regroupements ──────────────────────────────────────────────────────

    @Test
    fun `mythology theme keeps only its mythology and sub-groups by translated race alphabetically`() {
        val entites = listOf(
            ent(1, "Zeus", "Grecque", "God"),
            ent(2, "Typhon", "Grecque", "Monster"),
            ent(3, "Cronos", "Grecque", "Titan"),
            ent(4, "Jupiter", "Romaine", "God"),
        )
        val g = groups("mythologie_grecque", entites)

        assertEquals(listOf("Dieu", "Monstre", "Titan"), g.map { it.header })
        assertEquals(listOf("Zeus"), g[0].items.map { it.name })
        assertTrue("les entités non grecques sont exclues", g.flatMap { it.items }.none { it.name == "Jupiter" })
    }

    @Test
    fun `race theme sub-groups by mythology ordered by descending entity count`() {
        val entites = listOf(
            ent(1, "Zeus", "Grecque", "God"),
            ent(2, "Héra", "Grecque", "God"),
            ent(3, "Apollon", "Grecque", "God"),
            ent(4, "Jupiter", "Romaine", "God"),
            ent(5, "Fenrir", "Nordique", "Monster"),
        )
        val g = groups("dieux", entites)

        // Grecque (3 entités au total) passe avant Romaine (1) ; Nordique n'a aucun Dieu.
        assertEquals(listOf("Grecque", "Romaine"), g.map { it.header })
        assertEquals(listOf(3, 1), g.map { it.items.size })
    }

    @Test
    fun `global entites theme groups everything by ranked mythology`() {
        val entites = listOf(
            ent(1, "Zeus", "Grecque", "God"),
            ent(2, "Héra", "Grecque", "God"),
            ent(3, "Typhon", "Grecque", "Monster"),
            ent(4, "Jupiter", "Romaine", "God"),
            ent(5, "Odin", "Nordique", "God"),
        )
        val g = groups("entites", entites)

        assertEquals(listOf("Grecque", "Nordique", "Romaine"), g.map { it.header })
        assertEquals(listOf(3, 1, 1), g.map { it.items.size })
    }

    @Test
    fun `muses theme always exposes the Classiques and Beotiennes sub-groups`() {
        val entites = listOf(
            ent(1, "Calliope", "Grecque", "Muses", museType = "Grecque"),
            ent(2, "Mélété", "Grecque", "Muses", museType = "Beotienne"),
        )
        val g = groups("muses", entites)
        assertEquals(listOf("Classiques", "Béotiennes"), g.map { it.header })
        assertEquals(listOf("Calliope"), g[0].items.map { it.name })
        assertEquals(listOf("Mélété"), g[1].items.map { it.name })
    }

    @Test
    fun `zodiaque theme splits classic and chinese signs`() {
        val entites = listOf(
            ent(1, "Bélier", "Grecque", "Zodiacal_Sign", zodiacType = "Classique"),
            ent(2, "Rat", "Chinoise", "Zodiacal_Sign", zodiacType = "Chinois"),
        )
        val g = groups("zodiaque", entites)
        assertEquals(listOf("Classiques", "Chinois"), g.map { it.header })
    }

    @Test
    fun `artefacts theme returns a single group with every artifact and a filled detail text`() {
        val artifacts = listOf(art(1, "Mjöllnir"), art(2, "Gungnir"), art(3, "Excalibur"))
        val g = groups("artefacts", artifacts = artifacts)

        assertEquals(1, g.size)
        assertNull(g.single().header)
        assertEquals(listOf("Mjöllnir", "Gungnir", "Excalibur"), g.single().items.map { it.name })
        assertTrue(g.single().items.all { it.detailText.isNotBlank() })
    }

    @Test
    fun `resolveGroups preserves entity id and name on every ListItem`() {
        val entites = listOf(ent(42, "Athéna", "Grecque", "God"))
        val item = groups("mythologie_grecque", entites).flatMap { it.items }.single()
        assertEquals(42, item.id)
        assertEquals("Athéna", item.name)
        assertNotNull(item.detailText)
    }
}
