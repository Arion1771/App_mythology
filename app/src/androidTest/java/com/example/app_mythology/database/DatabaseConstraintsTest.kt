package com.example.app_mythology.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Vérifie le fonctionnement réel de la base Room et ses contraintes
 * testables (branche Test-Non-Regression) : schéma, round-trip des 5
 * tables, stratégie de conflit REPLACE de `entity_levels`, fenêtre des
 * dernières rencontres de `entity_encounters`, et cohérence de la clé
 * stable nom+mythologie+race (`EntityProgressKey`) sur les vraies données
 * de `prepopulate.json` une fois chargées en base.
 *
 * Base en mémoire, isolée du singleton de production (`AppDatabase.getInstance`,
 * qui persiste dans un vrai fichier) : chaque test repart d'une base vide.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DatabaseConstraintsTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    // ── Round-trip des 5 tables ─────────────────────────────────────────────

    @Test
    fun t01_entiteRoundTrip() = runBlocking {
        val e = EntiteEntity(name = "Test Entité", mythology = "Test", race = "God", difficulty = 2)
        db.entiteDao().insertAll(listOf(e))
        val all = db.entiteDao().getAllSync()
        assertEquals(1, all.size)
        assertEquals("Test Entité", all.first().name)
        assertEquals(2, all.first().difficulty)
    }

    @Test
    fun t02_artifactRoundTrip() = runBlocking {
        val a = ArtifactEntity(name = "Test Artéfact", mythology = "Test", artifactType = "Arme", difficulty = 1)
        db.artifactDao().insertAll(listOf(a))
        val all = db.artifactDao().getAllSync()
        assertEquals(1, all.size)
        assertEquals("Test Artéfact", all.first().name)
    }

    @Test
    fun t03_placeRoundTrip() = runBlocking {
        val p = PlaceEntity(name = "Test Lieu", mythology = "Test", description = "desc", placeType = "yggdrasil")
        db.placeDao().insertAll(listOf(p))
        val all = db.placeDao().getAllSync()
        assertEquals(1, all.size)
        assertEquals("Test Lieu", all.first().name)
    }

    @Test
    fun t04_entityEncounterAccumulatesMultipleRowsForSameKey() = runBlocking {
        val key = "Zeus|Grecque|God"
        db.entityEncounterDao().insert(EntityEncounterEntity(entityKey = key, faute = 0.0))
        db.entityEncounterDao().insert(EntityEncounterEntity(entityKey = key, faute = 1.0))
        db.entityEncounterDao().insert(EntityEncounterEntity(entityKey = key, faute = 0.5))

        assertEquals(3, db.entityEncounterDao().countForKey(key))
        val lastTwo = db.entityEncounterDao().lastFautesForKey(key, 2)
        assertEquals(2, lastTwo.size)
        // La plus récente rencontre (faute 0.5) doit être la première retournée.
        assertEquals(0.5, lastTwo.first(), 0.0001)
    }

    @Test
    fun t05_entityLevelReplaceConflictStrategyOverwritesExistingRow() = runBlocking {
        val key = "Zeus|Grecque|God"
        db.entityLevelDao().insertAll(listOf(EntityLevelEntity(entityKey = key, level = 1, contentHash = "h1")))
        db.entityLevelDao().insertAll(listOf(EntityLevelEntity(entityKey = key, level = 3, contentHash = "h2")))

        val stored = db.entityLevelDao().getByKey(key)
        assertEquals(3, stored?.level)
        assertEquals("h2", stored?.contentHash)

        // Une seule ligne doit exister pour cette clé (REPLACE, pas un doublon).
        val byKeys = db.entityLevelDao().getByKeys(listOf(key))
        assertEquals(1, byKeys.size)
    }

    @Test
    fun t06_deleteForKeysRemovesOnlyTargetedEntities() = runBlocking {
        db.entityEncounterDao().insert(EntityEncounterEntity(entityKey = "A", faute = 0.0))
        db.entityEncounterDao().insert(EntityEncounterEntity(entityKey = "B", faute = 0.0))
        db.entityLevelDao().insertAll(listOf(
            EntityLevelEntity(entityKey = "A", level = 1, contentHash = "h"),
            EntityLevelEntity(entityKey = "B", level = 2, contentHash = "h"),
        ))

        db.entityEncounterDao().deleteForKeys(listOf("A"))
        db.entityLevelDao().deleteForKeys(listOf("A"))

        assertEquals(0, db.entityEncounterDao().countForKey("A"))
        assertEquals(1, db.entityEncounterDao().countForKey("B"))
        assertEquals(null, db.entityLevelDao().getByKey("A"))
        assertEquals(2, db.entityLevelDao().getByKey("B")?.level)
    }

    // ── Cohérence de la clé stable sur les vraies données de prepopulate.json ──

    @Test
    fun t07_everyEntityFromPrepopulateJsonHasAUniqueStableKeyOnceLoaded() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val raw = context.assets.open("prepopulate.json").bufferedReader().use { it.readText() }
        val root = JSONObject(raw)
        val entitesJson = root.getJSONArray("entites")

        val entites = (0 until entitesJson.length()).map { i ->
            val o = entitesJson.getJSONObject(i)
            EntiteEntity(
                name = o.getString("name"),
                mythology = o.getString("mythology"),
                race = o.getString("race"),
                difficulty = if (o.has("difficulty") && !o.isNull("difficulty")) o.getInt("difficulty") else 1,
            )
        }
        db.entiteDao().insertAll(entites)

        val loaded = db.entiteDao().getAllSync()
        assertEquals("toutes les entités de prepopulate.json doivent être chargées", entites.size, loaded.size)

        val keys = loaded.map { EntityProgressKey.keyOf(it) }
        assertEquals(
            "la clé stable nom+mythologie+race doit être unique pour chaque entité une fois en base",
            keys.size, keys.toSet().size,
        )

        // Chaque entité doit toujours avoir un id Room valide (auto-généré, > 0).
        assertTrue("tous les id Room générés doivent être positifs", loaded.all { it.id > 0 })
    }
}
