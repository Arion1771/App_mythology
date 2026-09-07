package com.example.app_mythology.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * placeType : "Fleuve" | "Royaume" | "Enfers"
 */
@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val mythology: String,
    val description: String,
    val placeType: String,      // "Fleuve", "Royaume", "Enfers"

    // Fleuve
    val particularity: String? = null,

    // Royaume
    val inhabitants: String? = null,

    // Enfers — âmes qui y séjournent
    val souls: String? = null
)
