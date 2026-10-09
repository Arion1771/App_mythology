package com.example.app_mythology.quiz

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class AnswerNormalizationTest {

    private fun normalize(s: String): String =
        Normalizer.normalize(s.trim().lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")

    @Test
    fun `t01 comparison ignores case`() {
        assertEquals(normalize("Zeus"), normalize("zEUS"))
    }

    @Test
    fun `t02 comparison ignores leading and trailing whitespace`() {
        assertEquals(normalize("Odin"), normalize("   Odin  "))
    }

    @Test
    fun `t03 comparison ignores diacritics`() {
        assertEquals(normalize("Rê"), normalize("Re"))
        assertEquals(normalize("Épona"), normalize("epona"))
        assertEquals(normalize("Thialfï"), normalize("Thialfi"))
    }

    @Test
    fun `t04 a genuinely different answer does not match`() {
        assertNotEquals(normalize("Thor"), normalize("Loki"))
        assertNotEquals(normalize("Freyr"), normalize("Freyja"))
    }
}
