package com.example.app_mythology.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest

@Database(
    entities = [EntiteEntity::class, PlaceEntity::class, ArtifactEntity::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun entiteDao(): EntiteDao
    abstract fun placeDao(): PlaceDao
    abstract fun artifactDao(): ArtifactDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val PREFS_NAME = "db_sync"
        private const val KEY_JSON_HASH = "prepopulate_hash"

        /**
         * Ajout de userModified (par défaut vrai) sans purger la base : les lignes déjà en place
         * (qu'elles viennent de prepopulate.json ou de l'utilisateur) démarrent protégées, faute
         * de pouvoir distinguer les deux rétroactivement — seules les nouvelles resynchronisations
         * depuis prepopulate.json créeront désormais des lignes explicitement à userModified = 0.
         */
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE entites ADD COLUMN userModified INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE places ADD COLUMN userModified INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE artifacts ADD COLUMN userModified INTEGER NOT NULL DEFAULT 1")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mythobase.db"
                )
                .addMigrations(MIGRATION_6_7)
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
            }
        }

        /**
         * Peuple la base au premier lancement, puis la resynchronise avec prepopulate.json à
         * chaque changement de contenu du fichier (détecté par hash, comparé à celui du dernier
         * lancement) : les entrées absentes de la base sont ajoutées, celles déjà présentes et
         * jamais modifiées par l'utilisateur (userModified = false) sont mises à jour, et celles
         * modifiées ou créées par l'utilisateur (userModified = true, valeur par défaut) ne sont
         * jamais touchées.
         */
        suspend fun syncDatabase(context: Context) {
            val db = getInstance(context)
            try {
                val jsonText = context.assets.open("prepopulate.json")
                    .bufferedReader().use { it.readText() }
                val hash = sha256(jsonText)
                val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val root = JSONObject(jsonText)

                val count = db.entiteDao().countAll()
                if (count == 0) {
                    Log.d("AppDatabase", "Base vide — peuplement…")
                    insertFromJson(db, root)
                } else if (prefs.getString(KEY_JSON_HASH, null) != hash) {
                    Log.d("AppDatabase", "prepopulate.json modifié — resynchronisation…")
                    mergeFromJson(db, root)
                } else {
                    Log.d("AppDatabase", "Base déjà à jour ($count entrées)")
                }
                prefs.edit().putString(KEY_JSON_HASH, hash).apply()
            } catch (e: Exception) {
                Log.e("AppDatabase", "Erreur syncDatabase", e)
            }
        }

        private suspend fun insertFromJson(db: AppDatabase, root: JSONObject) {
            val entites = root.getJSONArray("entites")
            for (i in 0 until entites.length()) {
                db.entiteDao().insert(entiteFromJson(entites.getJSONObject(i)))
            }
            Log.d("AppDatabase", "✓ ${entites.length()} entités insérées")

            val places = root.getJSONArray("places")
            for (i in 0 until places.length()) {
                db.placeDao().insert(placeFromJson(places.getJSONObject(i)))
            }
            Log.d("AppDatabase", "✓ ${places.length()} lieux insérés")

            val artifacts = root.optJSONArray("artifacts") ?: JSONArray()
            for (i in 0 until artifacts.length()) {
                db.artifactDao().insert(artifactFromJson(artifacts.getJSONObject(i)))
            }
            Log.d("AppDatabase", "✓ ${artifacts.length()} artéfacts insérés")
        }

        private suspend fun mergeFromJson(db: AppDatabase, root: JSONObject) {
            val existingEntites = db.entiteDao().getAllSync()
                .associateBy { Triple(it.name, it.mythology, it.race) }
            var entitesAdded = 0
            var entitesUpdated = 0
            val entites = root.getJSONArray("entites")
            for (i in 0 until entites.length()) {
                val fresh = entiteFromJson(entites.getJSONObject(i))
                when (val existing = existingEntites[Triple(fresh.name, fresh.mythology, fresh.race)]) {
                    null -> { db.entiteDao().insert(fresh); entitesAdded++ }
                    else -> if (!existing.userModified) {
                        db.entiteDao().update(fresh.copy(id = existing.id))
                        entitesUpdated++
                    }
                }
            }
            Log.d("AppDatabase", "✓ Entités : $entitesAdded ajoutées, $entitesUpdated mises à jour")

            val existingPlaces = db.placeDao().getAllSync().associateBy { it.name to it.mythology }
            val places = root.getJSONArray("places")
            for (i in 0 until places.length()) {
                val fresh = placeFromJson(places.getJSONObject(i))
                when (val existing = existingPlaces[fresh.name to fresh.mythology]) {
                    null -> db.placeDao().insert(fresh)
                    else -> if (!existing.userModified) db.placeDao().update(fresh.copy(id = existing.id))
                }
            }

            val existingArtifacts = db.artifactDao().getAllSync().associateBy { it.name to it.mythology }
            val artifacts = root.optJSONArray("artifacts") ?: JSONArray()
            for (i in 0 until artifacts.length()) {
                val fresh = artifactFromJson(artifacts.getJSONObject(i))
                when (val existing = existingArtifacts[fresh.name to fresh.mythology]) {
                    null -> db.artifactDao().insert(fresh)
                    else -> if (!existing.userModified) db.artifactDao().update(fresh.copy(id = existing.id))
                }
            }
        }

        private fun entiteFromJson(o: JSONObject) = EntiteEntity(
            name              = o.optString("name"),
            mythology         = o.optString("mythology"),
            race              = o.optString("race"),
            clue              = o.ns("clue"),
            difficulty        = if (o.has("difficulty") && !o.isNull("difficulty"))
                                    o.getInt("difficulty") else 1,
            domain            = o.ns("domain"),
            godType           = o.ns("godType"),
            equivalentName    = o.ns("equivalentName"),
            fatherName        = o.ns("fatherName"),
            motherName        = o.ns("motherName"),
            giantType         = o.ns("giantType"),
            opponentName      = o.ns("opponentName"),
            story             = o.ns("story"),
            killer            = o.ns("killer"),
            ascendantName     = o.ns("ascendantName"),
            monsterType       = o.ns("monsterType"),
            description       = o.ns("description"),
            primordial        = if (o.has("primordial") && !o.isNull("primordial"))
                                    o.getBoolean("primordial") else null,
            museType          = o.ns("museType"),
            role              = o.ns("role"),
            death             = o.ns("death"),
            zodiacType        = o.ns("zodiacType"),
            chineseEquivalent = o.ns("chineseEquivalent"),
            popularCulture    = o.ns("popularCulture"),
            tags              = o.ns("tags"),
            listThemes        = o.ns("listThemes"),
            userModified      = false
        )

        private fun placeFromJson(o: JSONObject) = PlaceEntity(
            name          = o.optString("name"),
            mythology     = o.optString("mythology"),
            description   = o.optString("description"),
            placeType     = o.optString("placeType"),
            particularity = o.ns("particularity"),
            inhabitants   = o.ns("inhabitants"),
            souls         = o.ns("souls"),
            userModified  = false
        )

        private fun artifactFromJson(o: JSONObject) = ArtifactEntity(
            name         = o.optString("name"),
            mythology    = o.optString("mythology"),
            artifactType = o.optString("artifactType"),
            ownerName    = o.ns("ownerName"),
            creatorName  = o.ns("creatorName"),
            power        = o.ns("power"),
            story        = o.ns("story"),
            description  = o.ns("description"),
            clue         = o.ns("clue"),
            difficulty   = if (o.has("difficulty") && !o.isNull("difficulty"))
                                o.getInt("difficulty") else 1,
            tags         = o.ns("tags"),
            userModified = false
        )

        private fun JSONObject.ns(key: String): String? {
            if (!has(key) || isNull(key)) return null
            val v = optString(key)
            return if (v.isBlank() || v == "null") null else v
        }

        private fun sha256(text: String): String =
            MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
                .joinToString("") { "%02x".format(it) }
    }
}
