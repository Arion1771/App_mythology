package com.example.app_mythology.model.entity

import com.example.app_mythology.model.base.Entity

class Titan(
    name: String,
    mythology: String,
    val domain: String,
    val equivalent: Titan? = null
) : Entity(name, mythology)
