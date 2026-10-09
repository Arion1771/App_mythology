package com.example.app_mythology

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import com.example.app_mythology.database.AppDatabase
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Remet l'appareil dans l'état exact d'avant chaque test — branche
 * Test-Non-Regression.
 *
 * Les tests instrumentés tournent sur la vraie installation de l'application :
 * un quiz joué par un test enregistre des rencontres (`entity_encounters`), peut
 * figer des niveaux internes de difficulté (`entity_levels`) et débloquer des
 * succès (SharedPreferences `achievements`). Cette règle en prend une copie
 * avant le test et la restaure à l'identique après, même si le test échoue :
 * la progression et les niveaux internes du joueur de l'appareil ne sont pas
 * affectés par les tests.
 */
class DeviceStateRestoreRule : TestRule {

    private val tables = listOf("entity_encounters", "entity_levels")
    private val prefsNames = listOf("achievements")

    override fun apply(base: Statement, description: Description): Statement = object : Statement() {
        override fun evaluate() {
            val context = ApplicationProvider.getApplicationContext<Context>()
            val db = AppDatabase.getInstance(context).openHelper.writableDatabase
            val tableSnapshot = tables.associateWith { table ->
                db.query("SELECT * FROM $table").use { it.toRows() }
            }
            val prefsSnapshot = prefsNames.associateWith { name ->
                HashMap(context.getSharedPreferences(name, Context.MODE_PRIVATE).all)
            }
            try {
                base.evaluate()
            } finally {
                // Laisse se terminer les écritures lancées en arrière-plan par le test
                // (enregistrement des rencontres en coroutine) avant de restaurer.
                Thread.sleep(500)
                db.beginTransaction()
                try {
                    for ((table, rows) in tableSnapshot) {
                        db.execSQL("DELETE FROM $table")
                        rows.forEach { db.insert(table, SQLiteDatabase.CONFLICT_REPLACE, it) }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                for ((name, values) in prefsSnapshot) {
                    val editor = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear()
                    values.forEach { (key, value) -> editor.putAny(key, value) }
                    editor.commit()
                }
            }
        }
    }

    /** Toutes les lignes du curseur, colonnes et types d'origine compris (id inclus). */
    private fun Cursor.toRows(): List<ContentValues> {
        val rows = mutableListOf<ContentValues>()
        while (moveToNext()) {
            val row = ContentValues()
            for (i in 0 until columnCount) {
                val column = getColumnName(i)
                when (getType(i)) {
                    Cursor.FIELD_TYPE_NULL -> row.putNull(column)
                    Cursor.FIELD_TYPE_INTEGER -> row.put(column, getLong(i))
                    Cursor.FIELD_TYPE_FLOAT -> row.put(column, getDouble(i))
                    Cursor.FIELD_TYPE_BLOB -> row.put(column, getBlob(i))
                    else -> row.put(column, getString(i))
                }
            }
            rows += row
        }
        return rows
    }

    @Suppress("UNCHECKED_CAST")
    private fun android.content.SharedPreferences.Editor.putAny(key: String, value: Any?) {
        when (value) {
            is String -> putString(key, value)
            is Set<*> -> putStringSet(key, HashSet(value as Set<String>))
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            is Boolean -> putBoolean(key, value)
        }
    }
}
