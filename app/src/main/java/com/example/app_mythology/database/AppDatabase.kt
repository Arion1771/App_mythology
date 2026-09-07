package com.example.app_mythology.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest

@Database(
    entities = [EntiteEntity::class, PlaceEntity::class, ArtifactEntity::class],
    version = 8,
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

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mythobase.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
            }
        }

        /**
         * prepopulate.json est la base complète dans sa forme finale : il n'existe aucune
         * fonctionnalité d'ajout/modification/suppression depuis l'application. Au démarrage, si
         * le contenu du fichier a changé depuis le dernier lancement (comparaison par hash), la
         * base est intégralement vidée puis rechargée à l'identique du JSON.
         */
        suspend fun syncDatabase(context: Context) {
            val db = getInstance(context)
            try {
                val jsonText = context.assets.open("prepopulate.json")
                    .bufferedReader().use { it.readText() }
                val hash = sha256(jsonText)
                val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

                if (prefs.getString(KEY_JSON_HASH, null) == hash) {
                    Log.d("AppDatabase", "Base déjà à jour, aucun rechargement nécessaire")
                    return
                }

                Log.d("AppDatabase", "prepopulate.json modifié (ou premier lancement) — rechargement complet…")
                reloadFromJson(db, JSONObject(jsonText))
                prefs.edit().putString(KEY_JSON_HASH, hash).apply()
            } catch (e: Exception) {
                Log.e("AppDatabase", "Erreur syncDatabase", e)
            }
        }

        private suspend fun reloadFromJson(db: AppDatabase, root: JSONObject) {
            db.entiteDao().deleteAll()
            db.placeDao().deleteAll()
            db.artifactDao().deleteAll()

            val entites = root.getJSONArray("entites")
            db.entiteDao().insertAll((0 until entites.length()).map { entiteFromJson(entites.getJSONObject(it)) })
            Log.d("AppDatabase", "✓ ${entites.length()} entités chargées")

            val places = root.getJSONArray("places")
            db.placeDao().insertAll((0 until places.length()).map { placeFromJson(places.getJSONObject(it)) })
            Log.d("AppDatabase", "✓ ${places.length()} lieux chargés")

            val artifacts = root.optJSONArray("artifacts") ?: JSONArray()
            db.artifactDao().insertAll((0 until artifacts.length()).map { artifactFromJson(artifacts.getJSONObject(it)) })
            Log.d("AppDatabase", "✓ ${artifacts.length()} artéfacts chargés")
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
            listThemes        = o.ns("listThemes")
        )

        private fun placeFromJson(o: JSONObject) = PlaceEntity(
            name          = o.optString("name"),
            mythology     = o.optString("mythology"),
            description   = o.optString("description"),
            placeType     = o.optString("placeType"),
            particularity = o.ns("particularity"),
            inhabitants   = o.ns("inhabitants"),
            souls         = o.ns("souls")
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
            tags         = o.ns("tags")
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
