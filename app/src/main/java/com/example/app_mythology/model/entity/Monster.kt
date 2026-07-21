package com.example.app_mythology.model.entity

import com.example.app_mythology.model.base.Entity

class Monster(
    name: String,
    mythology: String,
    val type: String,
    val description: String
) : Entity(name, mythology)
