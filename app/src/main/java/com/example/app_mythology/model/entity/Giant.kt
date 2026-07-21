package com.example.app_mythology.model.entity

import com.example.app_mythology.model.base.Entity
import com.example.app_mythology.model.enum.GiantType

class Giant(
    name: String,
    mythology: String,
    val giantType: GiantType,
    val opponent: God? = null,
    val equivalent: Giant? = null
) : Entity(name, mythology)
