package com.example.app_mythology.achievements

import android.content.Context
import android.content.SharedPreferences
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.database.EntityProgressKey

/**
 * Persiste les succès débloqués dans des SharedPreferences dédiées, distinctes de
 * la base Room (intégralement vidée et rechargée depuis prepopulate.json à chaque
 * changement de son contenu, voir AppDatabase.syncDatabase) afin que les succès
 * survivent aux mises à jour de l'application.
 */
object AchievementManager {

    private const val PREFS_NAME = "achievements"
    private const val KEY_UNLOCKED = "unlocked_ids"
    private const val KEY_OBTAINED_ENTITY_KEYS = "obtained_entity_keys"
    /** Ancien stockage par nom seul, converti par [migrateLegacyObtainedNames]. */
    private const val KEY_LEGACY_OBTAINED_NAMES = "obtained_entity_names"

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

    private fun obtainedEntityKeys(): Set<String> =
        prefs.getStringSet(KEY_OBTAINED_ENTITY_KEYS, emptySet()) ?: emptySet()

    /**
     * Mémorise qu'une entité a été répondue correctement au moins une fois, pour les succès
     * de collection. Identifiée par sa clé nom+mythologie+race ([EntityProgressKey]) et non
     * par son seul nom, pour que deux homonymes (ex. Dragon chinois / Dragon européen) ne
     * comptent pas l'un pour l'autre.
     */
    fun markEntityObtained(entity: EntiteEntity) {
        val key = EntityProgressKey.keyOf(entity)
        val current = obtainedEntityKeys()
        if (key in current) return
        val updated = HashSet(current)
        updated.add(key)
        prefs.edit().putStringSet(KEY_OBTAINED_ENTITY_KEYS, updated).apply()
    }

    /**
     * Convertit l'ancien stockage par nom seul en clés nom+mythologie+race, puis le supprime.
     * Le nom seul ne permettant pas de savoir lequel de deux homonymes avait été obtenu, ils
     * sont tous considérés obtenus : aucune progression déjà acquise n'est perdue.
     */
    fun migrateLegacyObtainedNames(all: List<EntiteEntity>) {
        val legacyNames = prefs.getStringSet(KEY_LEGACY_OBTAINED_NAMES, null) ?: return
        val updated = HashSet(obtainedEntityKeys())
        all.filter { it.name in legacyNames }.mapTo(updated) { EntityProgressKey.keyOf(it) }
        prefs.edit()
            .putStringSet(KEY_OBTAINED_ENTITY_KEYS, updated)
            .remove(KEY_LEGACY_OBTAINED_NAMES)
            .apply()
    }

    /** Débloque [achievementId] si toutes les entités de [targets] ont déjà été obtenues au moins une fois. */
    fun unlockIfAllObtained(targets: Collection<EntiteEntity>, achievementId: String) {
        val targetKeys = targets.map { EntityProgressKey.keyOf(it) }
        if (targetKeys.isNotEmpty() && obtainedEntityKeys().containsAll(targetKeys)) {
            unlock(achievementId)
        }
    }
}
