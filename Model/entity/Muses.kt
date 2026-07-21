package com.example.app_mythology.Model.entity

import com.example.app_mythology.Model.base.Entity
import com.example.app_mythology.Model.enum.MuseType

class Muses(
    name: String,
    mythology: String,
    val domain: String,
    val museType: MuseType
) : Entity(name, mythology)
