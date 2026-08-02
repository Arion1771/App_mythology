package com.example.app_mythology.quiz

import com.example.app_mythology.database.ArtifactEntity
import com.example.app_mythology.database.EntiteEntity

/** Sélection des leurres de QCM par proximité thématique (tags, mythologie, race/type, équivalent). */

private fun tagsOf(raw: String?): Set<String> = raw?.split(",")?.map { it.trim() }?.toSet() ?: emptySet()

private fun similarityScore(correct: EntiteEntity, other: EntiteEntity): Int {
    var s = (tagsOf(correct.tags) intersect tagsOf(other.tags)).size * 3
    if (other.mythology == correct.mythology) s += 2
    if (other.race == correct.race) s += 2
    if (correct.godType != null && correct.godType == other.godType) s += 1
    if (correct.monsterType != null && correct.monsterType == other.monsterType) s += 1
    if (correct.equivalentName == other.name || other.equivalentName == correct.name) s += 4
    return s
}

private fun similarityScore(correct: ArtifactEntity, other: ArtifactEntity): Int {
    var s = (tagsOf(correct.tags) intersect tagsOf(other.tags)).size * 3
    if (other.mythology == correct.mythology) s += 2
    if (other.artifactType == correct.artifactType) s += 2
    return s
}

/** Nom correct + 3 leurres pris parmi les 12 entités les plus proches thématiquement, mélangés. */
fun pickQcmChoices(correct: EntiteEntity, pool: List<EntiteEntity>): List<String> {
    val decoys = pool.filter { it.id != correct.id }
        .sortedByDescending { similarityScore(correct, it) }
        .take(12).shuffled().take(3)
    return (listOf(correct.name) + decoys.map { it.name }).shuffled()
}

/** Nom correct + 3 leurres pris parmi les 12 artéfacts les plus proches thématiquement, mélangés. */
fun pickQcmChoices(correct: ArtifactEntity, pool: List<ArtifactEntity>): List<String> {
    val decoys = pool.filter { it.id != correct.id }
        .sortedByDescending { similarityScore(correct, it) }
        .take(12).shuffled().take(3)
    return (listOf(correct.name) + decoys.map { it.name }).shuffled()
}
