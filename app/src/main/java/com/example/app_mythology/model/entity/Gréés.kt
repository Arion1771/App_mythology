package com.example.app_mythology.model.entity

import com.example.app_mythology.model.base.Entity
import com.example.app_mythology.model.enum.MuseType

class Gréés(
    name: String,
    mythology: String,
    val domain: String,
) : Entity(name, mythology)
