package com.example.app_mythology.database

import java.security.MessageDigest

/**
 * Clé stable et empreinte de contenu d'une entité, utilisées pour faire
 * correspondre une [EntiteEntity] à son historique de rencontres/niveau
 * interne (tables entity_encounters/entity_levels) à travers les
 * rechargements complets de prepopulate.json — l'id Room auto-généré ne
 * survit pas à un rechargement, mais nom+mythologie+race si.
 */
object EntityProgressKey {

    /** nom+mythologie+race est unique sur l'ensemble des entités (vérifié sur les 515 entités actuelles). */
    fun keyOf(name: String, mythology: String, race: String): String =
        "$name|$mythology|$race"

    fun keyOf(e: EntiteEntity): String = keyOf(e.name, e.mythology, e.race)

    /**
     * Empreinte des seuls champs qui influent sur la question posée (nom,
     * mythologie, race, domaine, indice, difficulté canonique) : un
     * changement de description/popularCulture/tags ne doit pas réinitialiser
     * l'historique de l'entité.
     */
    fun contentHashOf(e: EntiteEntity): String =
        sha256(listOf(e.name, e.mythology, e.race, e.domain.orEmpty(), e.clue.orEmpty(), e.difficulty.toString())
            .joinToString("|"))

    private fun sha256(text: String): String =
        MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
