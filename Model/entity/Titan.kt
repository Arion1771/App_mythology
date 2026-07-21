package com.example.app_mythology.Model.entity

import com.example.app_mythology.Model.base.Entity

class Titan(
    name: String,
    mythology: String,
    val domain: String,
    val equivalent: Titan? = null
) : Entity(name, mythology)
