package com.example.app_mythology.Model.entity

import com.example.app_mythology.Model.base.Entity

class Cyclope(
    name: String,
    mythology: String,
    val primordial: Boolean,
    val story: String,
    val equivalent: Cyclope? = null
) : Entity(name, mythology)
