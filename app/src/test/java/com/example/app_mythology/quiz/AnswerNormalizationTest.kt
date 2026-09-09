package com.example.app_mythology.quiz

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.text.Normalizer

/**
 * Verrou sur la **convention de réponse** des quiz Classique / QCM / Liste,
 * telle que documentée dans l'écran d'aide : la comparaison ignore la casse,
 * les accents et les espaces de début/fin.
 *
 * `normalize` est aujourd'hui dupliquée en `private` dans
 * `QuizViewModel` et `DuelViewModel`. La fonction ci-dessous en reproduit la
 * définition exacte : toute évolution de la règle de comparaison doit être
 * répercutée ici **et** dans ces deux ViewModels (candidat à une extraction
 * ultérieure dans une fonction pure partagée).
 */
class AnswerNormalizationTest {

    private fun normalize(s: String): String =
        Normalizer.normalize(s.trim().lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")

    private fun matches(input: String, expected: String) = normalize(input) == normalize(expected)

    @Test
    fun `comparison ignores case`() {
        assertEquals(normalize("Zeus"), normalize("zEUS"))
    }

    @Test
    fun `comparison ignores leading and trailing whitespace`() {
        assertEquals(normalize("Odin"), normalize("   Odin  "))
    }

    @Test
    fun `comparison ignores diacritics`() {
        assertEquals(normalize("Rê"), normalize("Re"))
        assertEquals(normalize("Épona"), normalize("epona"))
        assertEquals(normalize("Thialfï"), normalize("Thialfi"))
    }

    @Test
    fun `a correct answer with mixed case accents and spaces still matches`() {
        assertEquals(true, matches("  hÉraclÈs ", "Héraclès"))
    }

    @Test
    fun `a genuinely different answer does not match`() {
        assertNotEquals(normalize("Thor"), normalize("Loki"))
        assertNotEquals(normalize("Freyr"), normalize("Freyja"))
    }
}
