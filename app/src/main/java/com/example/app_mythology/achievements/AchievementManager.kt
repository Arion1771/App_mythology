package com.example.app_mythology.achievements

import android.content.Context
import android.content.SharedPreferences

/**
 * Persiste les succès débloqués dans des SharedPreferences dédiées, distinctes
 * de la base Room (dont le contenu par défaut peut être resynchronisé, voire
 * purgée en dernier recours par fallbackToDestructiveMigration en l'absence de
 * migration explicite) afin que les succès survivent aux mises à jour de l'application.
 */
object AchievementManager {

    private const val PREFS_NAME = "achievements"
    private const val KEY_UNLOCKED = "unlocked_ids"

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
}
