package com.example.app_mythology.quiz

import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.database.EntityLevelDao
import com.example.app_mythology.database.EntityEncounterDao
import com.example.app_mythology.database.EntityEncounterEntity
import com.example.app_mythology.database.EntityLevelEntity
import com.example.app_mythology.database.EntityProgressKey

/**
 * Difficulté adaptative des entités en quiz solo (Classique + QCM uniquement —
 * Duel et Artéfact n'y participent pas). Chaque entité a un niveau canonique
 * fixe (1-3, champ `difficulty`) et, en plus, un niveau interne (1-3) propre
 * au joueur de cet appareil, calculé à partir de ses performances récentes.
 *
 * Tant qu'il reste, dans le niveau canonique choisi, au moins une entité avec
 * moins de [BOOTSTRAP_THRESHOLD] rencontres (phase « bootstrap »), le quiz
 * pioche 15 questions au hasard dans tout le niveau canonique, sans
 * distinction de niveau interne (il n'y en a pas encore), et les points d'une
 * question valent la difficulté canonique. Une fois que TOUTES les entités du
 * niveau canonique ont atteint [BOOTSTRAP_THRESHOLD] rencontres, leur niveau
 * interne est calculé une fois pour toutes (figé définitivement, jamais
 * recalculé ensuite) à partir du nombre de fautes sur leurs 10 dernières
 * rencontres ; le quiz pioche alors 5 questions par niveau interne (1/2/3),
 * en empruntant au niveau interne voisin si l'un des trois est incomplet.
 */
object EntityDifficultyEngine {

    const val BOOTSTRAP_THRESHOLD = 10
    private const val HISTORY_WINDOW = 10
    private const val QUESTIONS_PER_QUIZ = 15
    private const val QUESTIONS_PER_INNER_LEVEL = 5

    /** Une question du quiz, avec le niveau (canonique en bootstrap, interne sinon) qui en fixe les points. */
    data class QuizQuestion(val entity: EntiteEntity, val pointsLevel: Int)

    /**
     * Construit le pool de 15 questions (ou moins si le niveau canonique ne
     * contient pas assez d'entités) pour le niveau canonique [canonicalDifficulty].
     */
    suspend fun buildQuizPool(
        encounterDao: EntityEncounterDao,
        levelDao: EntityLevelDao,
        allOfCanonicalLevel: List<EntiteEntity>,
        canonicalDifficulty: Int
    ): List<QuizQuestion> {
        if (allOfCanonicalLevel.isEmpty()) return emptyList()

        val byKey = allOfCanonicalLevel.associateBy { EntityProgressKey.keyOf(it) }
        val keys = byKey.keys.toList()
        val counts = encounterDao.countForKeys(keys).associate { it.entityKey to it.cnt }

        val stillBootstrapping = keys.any { (counts[it] ?: 0) < BOOTSTRAP_THRESHOLD }

        if (stillBootstrapping) {
            return allOfCanonicalLevel.shuffled().take(QUESTIONS_PER_QUIZ)
                .map { QuizQuestion(it, canonicalDifficulty) }
        }

        // Tout le niveau canonique a atteint le seuil : fige les niveaux internes
        // qui ne le sont pas encore, puis pioche 5 questions par niveau interne.
        freezeMissingLevels(encounterDao, levelDao, byKey, keys)
        val levels = levelDao.getByKeys(keys).associate { it.entityKey to it.level }

        val byInnerLevel = (1..3).associateWith { lvl ->
            allOfCanonicalLevel.filter { levels[EntityProgressKey.keyOf(it)] == lvl }
        }

        val pool = mutableListOf<QuizQuestion>()
        val used = mutableSetOf<String>()
        for (target in 1..3) {
            val picked = pickWithNeighborFallback(byInnerLevel, target, QUESTIONS_PER_INNER_LEVEL, used)
            pool += picked.map { QuizQuestion(it, target) }
        }
        return pool.shuffled()
    }

    /** Enregistre une rencontre et, si elle fait atteindre le seuil à toute la fratrie, fige les niveaux internes. */
    suspend fun recordEncounter(
        encounterDao: EntityEncounterDao,
        entity: EntiteEntity,
        faute: Double
    ) {
        val key = EntityProgressKey.keyOf(entity)
        encounterDao.insert(EntityEncounterEntity(entityKey = key, faute = faute))
    }

    private suspend fun freezeMissingLevels(
        encounterDao: EntityEncounterDao,
        levelDao: EntityLevelDao,
        byKey: Map<String, EntiteEntity>,
        keys: List<String>
    ) {
        val alreadyFrozen = levelDao.getByKeys(keys).map { it.entityKey }.toSet()
        val toFreeze = keys.filter { it !in alreadyFrozen }
        if (toFreeze.isEmpty()) return

        val newLevels = toFreeze.map { key ->
            val fautes = encounterDao.lastFautesForKey(key, HISTORY_WINDOW).sum()
            val level = when {
                fautes <= 3.0 -> 1
                fautes >= 8.0 -> 3
                else -> 2
            }
            val entity = byKey[key]!!
            EntityLevelEntity(entityKey = key, level = level, contentHash = EntityProgressKey.contentHashOf(entity))
        }
        levelDao.insertAll(newLevels)
    }

    /**
     * Pioche [count] entités parmi celles de niveau interne [target], et si le
     * palier est incomplet, complète avec les paliers voisins du plus proche
     * au plus éloigné (parmi le même niveau canonique), sans réutiliser une
     * entité déjà piochée pour un autre palier.
     */
    private fun pickWithNeighborFallback(
        byInnerLevel: Map<Int, List<EntiteEntity>>,
        target: Int,
        count: Int,
        used: MutableSet<String>
    ): List<EntiteEntity> {
        val result = mutableListOf<EntiteEntity>()
        val order = listOf(target) + listOf(1, 2, 3).filter { it != target }
            .sortedBy { kotlin.math.abs(it - target) }
        for (lvl in order) {
            if (result.size >= count) break
            val available = (byInnerLevel[lvl] ?: emptyList())
                .filter { EntityProgressKey.keyOf(it) !in used }
                .shuffled()
            for (e in available) {
                if (result.size >= count) break
                result += e
                used += EntityProgressKey.keyOf(e)
            }
        }
        return result
    }
}
