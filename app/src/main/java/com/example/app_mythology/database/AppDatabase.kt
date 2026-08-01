package com.example.app_mythology.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.json.JSONObject

@Database(
    entities = [EntiteEntity::class, PlaceEntity::class, ArtifactEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun entiteDao(): EntiteDao
    abstract fun placeDao(): PlaceDao
    abstract fun artifactDao(): ArtifactDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

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

        suspend fun populateIfEmpty(context: Context) {
            val db = getInstance(context)
            try {
                val count = db.entiteDao().countAll()
                Log.d("AppDatabase", "Entités en base : $count")
                if (count == 0) {
                    Log.d("AppDatabase", "Base vide — peuplement…")
                    insertFromJson(context, db)
                } else {
                    Log.d("AppDatabase", "Base déjà peuplée ($count entrées)")
                }
            } catch (e: Exception) {
                Log.e("AppDatabase", "Erreur populateIfEmpty", e)
            }
        }

        private suspend fun insertFromJson(context: Context, db: AppDatabase) {
            val json = context.assets.open("prepopulate.json")
                .bufferedReader().use { it.readText() }
            val root = JSONObject(json)

            val entites = root.getJSONArray("entites")
            for (i in 0 until entites.length()) {
                val o = entites.getJSONObject(i)
                db.entiteDao().insert(EntiteEntity(
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
                ))
            }
            Log.d("AppDatabase", "✓ ${entites.length()} entités insérées")

            val places = root.getJSONArray("places")
            for (i in 0 until places.length()) {
                val o = places.getJSONObject(i)
                db.placeDao().insert(PlaceEntity(
                    name          = o.optString("name"),
                    mythology     = o.optString("mythology"),
                    description   = o.optString("description"),
                    placeType     = o.optString("placeType"),
                    particularity = o.ns("particularity"),
                    inhabitants   = o.ns("inhabitants"),
                    souls         = o.ns("souls")
                ))
            }
            Log.d("AppDatabase", "✓ ${places.length()} lieux insérés")

            val artifacts = root.optJSONArray("artifacts") ?: org.json.JSONArray()
            for (i in 0 until artifacts.length()) {
                val o = artifacts.getJSONObject(i)
                db.artifactDao().insert(ArtifactEntity(
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
                ))
            }
            Log.d("AppDatabase", "✓ ${artifacts.length()} artéfacts insérés")
        }

        private fun JSONObject.ns(key: String): String? {
            if (!has(key) || isNull(key)) return null
            val v = optString(key)
            return if (v.isBlank() || v == "null") null else v
        }
    }
}
