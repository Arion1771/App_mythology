package com.example.app_mythology.Model.entity

import com.example.app_mythology.Model.base.Entity

class Hecatoncheires(
    name: String,
    mythology: String,
    val equivalent: Hecatoncheires? = null
) : Entity(name, mythology)
