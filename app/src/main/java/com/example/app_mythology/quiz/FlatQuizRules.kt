package com.example.app_mythology.quiz

/**
 * Règles des quiz sans difficulté interne (Artéfacts en Classique/QCM, et
 * Duel Classique/QCM) : même format que le quiz d'entités — 15 questions
 * puisées dans le seul niveau canonique choisi — mais chaque question vaut
 * le même nombre de points, quelle que soit sa difficulté : 2 points pour
 * une bonne réponse au 1er essai, 1 au 2e essai (Classique), 0 sinon, soit
 * un quiz sur 30 points.
 */
object FlatQuizRules {

    const val QUESTIONS_PER_QUIZ = 15
    const val POINTS_PER_QUESTION = 2.0

    /**
     * Pioche [count] éléments de niveau [target] (1-3) dans [all]. Si le
     * niveau n'en contient pas assez (ex. 12 artéfacts de niveau 1), complète
     * avec les niveaux voisins, du plus proche au plus éloigné (le plus facile
     * d'abord à distance égale). Le résultat est trié par priorité (niveau
     * choisi d'abord, chaque niveau mélangé) : à l'appelant de le mélanger.
     */
    fun <T> pickFromLevel(all: List<T>, target: Int, count: Int, levelOf: (T) -> Int): List<T> {
        val byLevel = all.groupBy(levelOf)
        val order = listOf(1, 2, 3).sortedWith(compareBy({ kotlin.math.abs(it - target) }, { it }))
        return order.flatMap { (byLevel[it] ?: emptyList()).shuffled() }.take(count)
    }
}
