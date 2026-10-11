package com.example.app_mythology.quiz

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.io.File

/**
 * Tests unitaires de [FlatQuizRules.pickFromLevel] (sélection des questions
 * des quiz Artéfacts et du Duel) : niveau choisi en priorité, complément par
 * le niveau voisin le plus proche (le plus facile à distance égale), jamais
 * de doublon. Le mélange interne à chaque niveau n'est pas testé.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class FlatQuizRulesTest {

    private data class Item(val id: Int, val level: Int)

    /** [n1] éléments de niveau 1, [n2] de niveau 2, [n3] de niveau 3, identifiants uniques. */
    private fun pool(n1: Int, n2: Int, n3: Int): List<Item> =
        List(n1) { Item(100 + it, 1) } + List(n2) { Item(200 + it, 2) } + List(n3) { Item(300 + it, 3) }

    private fun pick(all: List<Item>, target: Int, count: Int = FlatQuizRules.QUESTIONS_PER_QUIZ) =
        FlatQuizRules.pickFromLevel(all, target, count) { it.level }

    @Test
    fun `t01 a quiz is worth 30 points`() {
        assertEquals(30.0, FlatQuizRules.QUESTIONS_PER_QUIZ * FlatQuizRules.POINTS_PER_QUESTION, 0.0)
    }

    @Test
    fun `t02 enough items in the chosen level - only that level is used`() {
        for (target in 1..3) {
            val picked = pick(pool(20, 20, 20), target)
            assertEquals(15, picked.size)
            assertTrue("niveau $target", picked.all { it.level == target })
        }
    }

    @Test
    fun `t03 short level is topped up by the nearest level, easier first on a tie`() {
        // Facile : 12 de niveau 1, complété par le niveau 2 (seul voisin à distance 1).
        val easy = pick(pool(12, 14, 17), 1)
        assertEquals(listOf(12, 3, 0), (1..3).map { l -> easy.count { it.level == l } })

        // Moyen : 14 de niveau 2, complété par le niveau 1 (à égalité avec le 3, le plus facile d'abord).
        val medium = pick(pool(12, 14, 17), 2)
        assertEquals(listOf(1, 14, 0), (1..3).map { l -> medium.count { it.level == l } })

        // Difficile : niveau 3 vide, complété par le 2, puis par le 1.
        val hard = pick(pool(5, 4, 0), 3, 7)
        assertEquals(listOf(3, 4, 0), (1..3).map { l -> hard.count { it.level == l } })
    }

    @Test
    fun `t04 chosen level comes first, no duplicate, never more than available`() {
        val all = pool(12, 14, 17)
        val picked = pick(all, 1, 20)
        assertEquals(picked.size, picked.distinct().size)
        assertTrue("niveau choisi en tête", picked.take(12).all { it.level == 1 })
        assertEquals(all.size, pick(all, 2, 1000).size)
        assertEquals(emptyList<Item>(), pick(emptyList(), 1))
    }

    /** Les vraies données : chaque niveau d'artéfacts donne bien un quiz complet de 15 questions. */
    @Test
    fun `t05 real artifact data fills a 15-question quiz at every level`() {
        val f = listOf("src/main/assets/prepopulate.json", "app/src/main/assets/prepopulate.json")
            .map(::File).firstOrNull { it.exists() }
            ?: error("prepopulate.json introuvable (cwd=${File(".").absolutePath})")
        val artifacts = JSONObject(f.readText()).getJSONArray("artifacts")
        val items = (0 until artifacts.length()).map {
            val o = artifacts.getJSONObject(it)
            Item(it, if (o.has("difficulty") && !o.isNull("difficulty")) o.getInt("difficulty") else 1)
        }
        for (target in 1..3) assertEquals("niveau $target", 15, pick(items, target).size)
    }
}
