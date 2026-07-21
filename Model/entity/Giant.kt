package com.example.app_mythology.Model.entity

import com.example.app_mythology.Model.base.Entity
import com.example.app_mythology.Model.enum.GiantType

class Giant(
    name: String,
    mythology: String,
    val giantType: GiantType,
    val opponent: God? = null,
    val equivalent: Giant? = null
) : Entity(name, mythology)
