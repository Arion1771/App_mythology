package com.example.app_mythology.achievements

/**
 * [method] décrit la condition d'obtention, affichée uniquement une fois le
 * succès débloqué (les succès verrouillés n'affichent que le nom et l'icône).
 */
data class Achievement(
    val id: String,
    val name: String,
    val method: String,
    val category: String
)
