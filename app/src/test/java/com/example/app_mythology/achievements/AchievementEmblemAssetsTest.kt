package com.example.app_mythology.achievements

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Verrou de complétude des emblèmes de succès (branche Test-Unit) : chaque
 * succès du catalogue doit avoir son image `assets/achievements/<id>.png`.
 * `AchievementsFragment.loadIcon` retombe silencieusement sur un blason
 * générique quand le fichier manque — ce test empêche qu'un succès reste
 * sans emblème sans qu'on le remarque (cf. V4.1.1).
 */
class AchievementEmblemAssetsTest {

    private val dir: File = listOf("src/main/assets/achievements", "app/src/main/assets/achievements")
        .map(::File).firstOrNull { it.isDirectory }
        ?: error("dossier assets/achievements introuvable (cwd=${File(".").absolutePath})")

    @Test
    fun `every catalog achievement has an emblem png`() {
        val missing = AchievementCatalog.all
            .map { it.id }
            .filter { !File(dir, "$it.png").isFile }
        assertTrue("succès sans emblème dans assets/achievements/ : $missing", missing.isEmpty())
    }

    @Test
    fun `no orphan emblem png without a matching achievement`() {
        val ids = AchievementCatalog.all.map { it.id }.toSet()
        val orphans = dir.listFiles { f -> f.extension == "png" }
            .orEmpty()
            .map { it.nameWithoutExtension }
            .filter { it !in ids }
        assertTrue("emblèmes sans succès correspondant : $orphans", orphans.isEmpty())
    }

    @Test
    fun `catalog ids are unique`() {
        val ids = AchievementCatalog.all.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }
}
