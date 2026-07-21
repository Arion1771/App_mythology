package com.example.app_mythology.Model.entity

import com.example.app_mythology.Model.base.Entity

class Heroes(
    name: String,
    mythology: String,
    val story: String,
    val killer: String? = null,
    val ascendant: God? = null
) : Entity(name, mythology)
