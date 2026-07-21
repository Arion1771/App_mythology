package com.example.app_mythology.model.entity

import com.example.app_mythology.model.base.Entity

class Hecatoncheires(
    name: String,
    mythology: String,
    val equivalent: Hecatoncheires? = null
) : Entity(name, mythology)
