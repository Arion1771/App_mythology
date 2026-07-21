package com.example.app_mythology.model.entity

import com.example.app_mythology.model.base.Entity

class Cyclope(
    name: String,
    mythology: String,
    val primordial: Boolean,
    val story: String,
    val equivalent: Cyclope? = null
) : Entity(name, mythology)
