package com.example.app_mythology.Model.entity

import com.example.app_mythology.Model.base.Entity
import com.example.app_mythology.Model.enum.GodType

class God(
    name: String,
    mythology: String,
    val domain: String,
    val godType: GodType? = null,
    val equivalent: God? = null,
    val father: God? = null,
    val mother: God? = null
) : Entity(name, mythology)
