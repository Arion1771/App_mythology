package com.example.app_mythology.quiz

import com.example.app_mythology.database.ArtifactEntity
import com.example.app_mythology.database.EntiteEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests unitaires de [pickQcmChoices] (sélection des 4 propositions d'un QCM).
 *
 * `pickQcmChoices` mélange ses résultats ([shuffled]) : seules les propriétés
 * structurelles sont déterministes et donc testées ici (nombre de choix,
 * présence du bon nom, unicité, provenance du pool). Le classement fin des
 * leurres par proximité thématique est couvert indirectement : il ne peut pas
 * introduire de doublon ni faire disparaître la bonne réponse.
 */
class QcmDecoysTest {

    private fun ent(id: Int, name: String, mythology: String = "Grecque", race: String = "God",
                    tags: String? = null, godType: String? = null, equivalentName: String? = null) =
        EntiteEntity(id = id, name = name, mythology = mythology, race = race,
            tags = tags, godType = godType, equivalentName = equivalentName)

    private fun art(id: Int, name: String, mythology: String = "Nordique",
                    artifactType: String = "Arme", tags: String? = null) =
        ArtifactEntity(id = id, name = name, mythology = mythology, artifactType = artifactType, tags = tags)

    private val entityPool = listOf(
        ent(1, "Zeus", tags = "Foudre, Ciel", godType = "Olympien"),
        ent(2, "Poséidon", tags = "Eau, Mer", godType = "Olympien"),
        ent(3, "Hadès", tags = "Mort", godType = "Olympien"),
        ent(4, "Arès", tags = "Guerre", godType = "Olympien"),
        ent(5, "Apollon", tags = "Soleil, Arts", godType = "Olympien"),
        ent(6, "Héra", tags = "Royauté", godType = "Olympien"),
        ent(7, "Thor", mythology = "Nordique", tags = "Foudre"),
        ent(8, "Odin", mythology = "Nordique", tags = "Sagesse"),
    )

    private val artifactPool = listOf(
        art(1, "Mjöllnir", tags = "Foudre"),
        art(2, "Gungnir", tags = "Guerre"),
        art(3, "Excalibur", mythology = "Arthurienne", tags = "Royauté"),
        art(4, "Durendal", mythology = "Carolingienne", tags = "Guerre"),
        art(5, "Égide", mythology = "Grecque", artifactType = "Objet magique", tags = "Gardien"),
    )

    @Test
    fun `entity qcm returns four distinct choices`() {
        val correct = entityPool.first { it.name == "Zeus" }
        val choices = pickQcmChoices(correct, entityPool)

        assertEquals(4, choices.size)
        assertEquals("les 4 propositions doivent être distinctes", 4, choices.toSet().size)
    }

    @Test
    fun `entity qcm always contains the correct name exactly once`() {
        val correct = entityPool.first { it.name == "Poséidon" }
        repeat(50) {
            val choices = pickQcmChoices(correct, entityPool)
            assertEquals(1, choices.count { it == "Poséidon" })
        }
    }

    @Test
    fun `entity qcm decoys are drawn from the pool and never equal the correct entity`() {
        val correct = entityPool.first { it.name == "Apollon" }
        val poolNames = entityPool.map { it.name }.toSet()
        repeat(50) {
            val choices = pickQcmChoices(correct, entityPool)
            assertTrue("toutes les propositions viennent du pool", poolNames.containsAll(choices))
        }
    }

    @Test
    fun `artifact qcm returns four distinct choices including the correct one`() {
        val correct = artifactPool.first { it.name == "Mjöllnir" }
        val choices = pickQcmChoices(correct, artifactPool)

        assertEquals(4, choices.size)
        assertEquals(4, choices.toSet().size)
        assertTrue(choices.contains("Mjöllnir"))
    }

    @Test
    fun `artifact qcm decoys are drawn from the pool`() {
        val correct = artifactPool.first { it.name == "Gungnir" }
        val poolNames = artifactPool.map { it.name }.toSet()
        repeat(50) {
            val choices = pickQcmChoices(correct, artifactPool)
            assertTrue(poolNames.containsAll(choices))
            assertEquals(1, choices.count { it == "Gungnir" })
        }
    }
}
