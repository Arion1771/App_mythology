package com.example.app_mythology.achievements

import android.content.Context
import android.content.SharedPreferences

/**
 * Persiste les succès débloqués dans des SharedPreferences dédiées, distinctes de
 * la base Room (intégralement vidée et rechargée depuis prepopulate.json à chaque
 * changement de son contenu, voir AppDatabase.syncDatabase) afin que les succès
 * survivent aux mises à jour de l'application.
 */
object AchievementManager {

    private const val PREFS_NAME = "achievements"
    private const val KEY_UNLOCKED = "unlocked_ids"
    private const val KEY_OBTAINED_ENTITIES = "obtained_entity_names"

    private lateinit var prefs: SharedPreferences

    /** Posé par MainActivity pour afficher le bandeau de succès débloqué. */
    var bannerListener: ((Achievement) -> Unit)? = null

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isUnlocked(id: String): Boolean = id in unlockedIds()

    fun unlockedIds(): Set<String> = prefs.getStringSet(KEY_UNLOCKED, emptySet()) ?: emptySet()

    /** Débloque [id] s'il ne l'est pas déjà. Retourne true si nouvellement débloqué. */
    fun unlock(id: String): Boolean {
        val achievement = AchievementCatalog.byId(id) ?: return false
        val current = unlockedIds()
        if (id in current) return false
        // Copie défensive : le Set retourné par getStringSet ne doit jamais être muté en place.
        val updated = HashSet(current)
        updated.add(id)
        prefs.edit().putStringSet(KEY_UNLOCKED, updated).apply()
        bannerListener?.invoke(achievement)
        return true
    }

    /** Débloque [metaId] si tous les succès de [memberIds] sont déjà obtenus. */
    fun unlockGroupMeta(memberIds: List<String>, metaId: String) {
        val current = unlockedIds()
        if (memberIds.all { it in current }) {
            unlock(metaId)
        }
    }

    private fun obtainedEntityNames(): Set<String> =
        prefs.getStringSet(KEY_OBTAINED_ENTITIES, emptySet()) ?: emptySet()

    /** Mémorise qu'une entité a été répondue correctement au moins une fois, pour les succès de collection. */
    fun markEntityObtained(name: String) {
        val current = obtainedEntityNames()
        if (name in current) return
        val updated = HashSet(current)
        updated.add(name)
        prefs.edit().putStringSet(KEY_OBTAINED_ENTITIES, updated).apply()
    }

    /** Débloque [achievementId] si toutes les entités de [targetNames] ont déjà été obtenues au moins une fois. */
    fun unlockIfAllObtained(targetNames: Set<String>, achievementId: String) {
        if (targetNames.isNotEmpty() && obtainedEntityNames().containsAll(targetNames)) {
            unlock(achievementId)
        }
    }
}
