package com.example.app_mythology.model.entity

import com.example.app_mythology.model.base.Entity
import com.example.app_mythology.model.enum.MuseType

class Muses(
    name: String,
    mythology: String,
    val domain: String,
    val museType: MuseType
) : Entity(name, mythology)
