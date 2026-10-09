package com.example.app_mythology.n5_database

import androidx.room.Room
import com.example.app_mythology.database.AppDatabase
import com.example.app_mythology.database.ArtifactEntity
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.database.EntityEncounterEntity
import com.example.app_mythology.database.EntityLevelEntity
import com.example.app_mythology.database.EntityProgressKey
import com.example.app_mythology.database.PlaceEntity
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

    /**
     * Migration 9 → 10 (suppression de opponentName et chineseEquivalent) sur une
     * base au format V9 avec des données : Room doit l'ouvrir sans recourir à une
     * migration destructive, les entités doivent être recopiées à l'identique et
     * l'historique de difficulté adaptative du joueur (rencontres, niveaux
     * internes) conservé.
     */
    @Test
    fun t08_migration9To10KeepsEntitiesAndPlayerProgress() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val name = "migration_9_10_test.db"
        context.deleteDatabase(name)

        // Base au format V9 : `entites` avec les deux colonnes supprimées depuis.
        android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(name), null).use { v9 ->
            v9.execSQL("CREATE TABLE `entites` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `mythology` TEXT NOT NULL, `race` TEXT NOT NULL, `clue` TEXT, `difficulty` INTEGER NOT NULL, `domain` TEXT, `godType` TEXT, `equivalentName` TEXT, `fatherName` TEXT, `motherName` TEXT, `giantType` TEXT, `opponentName` TEXT, `story` TEXT, `killer` TEXT, `ascendantName` TEXT, `monsterType` TEXT, `description` TEXT, `primordial` INTEGER, `museType` TEXT, `role` TEXT, `death` TEXT, `zodiacType` TEXT, `chineseEquivalent` TEXT, `popularCulture` TEXT, `tags` TEXT, `listThemes` TEXT)")
            v9.execSQL("CREATE TABLE `places` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `mythology` TEXT NOT NULL, `description` TEXT NOT NULL, `placeType` TEXT NOT NULL, `particularity` TEXT, `inhabitants` TEXT, `souls` TEXT)")
            v9.execSQL("CREATE TABLE `artifacts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `mythology` TEXT NOT NULL, `artifactType` TEXT NOT NULL, `ownerName` TEXT, `creatorName` TEXT, `power` TEXT, `story` TEXT, `description` TEXT, `clue` TEXT, `difficulty` INTEGER NOT NULL, `tags` TEXT)")
            v9.execSQL("CREATE TABLE `entity_encounters` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `entityKey` TEXT NOT NULL, `faute` REAL NOT NULL)")
            v9.execSQL("CREATE TABLE `entity_levels` (`entityKey` TEXT NOT NULL, `level` INTEGER NOT NULL, `contentHash` TEXT NOT NULL, PRIMARY KEY(`entityKey`))")
            v9.execSQL("CREATE TABLE room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
            v9.execSQL("INSERT INTO room_master_table (id,identity_hash) VALUES(42, 'schema-v9')")

            v9.execSQL("INSERT INTO `entites` (`id`, `name`, `mythology`, `race`, `clue`, `difficulty`, `domain`, `godType`, `equivalentName`, `fatherName`, `motherName`, `giantType`, `opponentName`, `zodiacType`, `chineseEquivalent`, `primordial`, `popularCulture`, `tags`, `listThemes`) VALUES (7, 'Zeus', 'Grecque', 'God', 'Roi des dieux', 1, 'Ciel', 'Olympien', 'Jupiter', 'Cronos', 'Rhéa', NULL, 'Typhon', NULL, 'Inutile', 0, 'Smite', 'Ciel', 'Olympiens')")
            v9.execSQL("INSERT INTO `places` (`name`, `mythology`, `description`, `placeType`) VALUES ('Styx', 'Grecque', 'Fleuve', 'Fleuve')")
            v9.execSQL("INSERT INTO `entity_encounters` (`entityKey`, `faute`) VALUES ('zeus|grecque|god', 0.0), ('zeus|grecque|god', 1.0)")
            v9.execSQL("INSERT INTO `entity_levels` (`entityKey`, `level`, `contentHash`) VALUES ('zeus|grecque|god', 2, 'h')")
            v9.version = 9
        }

        // Ouverture par Room avec la seule migration fournie (aucun repli destructif).
        val migrated = Room.databaseBuilder(context, AppDatabase::class.java, name)
            .addMigrations(AppDatabase.MIGRATION_9_10)
            .build()
        try {
            val zeus = migrated.entiteDao().getAllSync().single()
            assertEquals(7, zeus.id)
            assertEquals("Zeus", zeus.name)
            assertEquals("Grecque", zeus.mythology)
            assertEquals("God", zeus.race)
            assertEquals("Roi des dieux", zeus.clue)
            assertEquals(1, zeus.difficulty)
            assertEquals("Ciel", zeus.domain)
            assertEquals("Olympien", zeus.godType)
            assertEquals("Jupiter", zeus.equivalentName)
            assertEquals("Cronos", zeus.fatherName)
            assertEquals("Rhéa", zeus.motherName)
            assertEquals(false, zeus.primordial)
            assertEquals("Smite", zeus.popularCulture)
            assertEquals("Ciel", zeus.tags)
            assertEquals("Olympiens", zeus.listThemes)

            assertEquals(1, migrated.placeDao().getAllSync().size)
            assertEquals("rencontres conservées", 2, migrated.entityEncounterDao().countForKey("zeus|grecque|god"))
            assertEquals("niveau interne conservé", 2, migrated.entityLevelDao().getByKey("zeus|grecque|god")?.level)
        } finally {
            migrated.close()
            context.deleteDatabase(name)
        }
    }
}
