package com.example.app_mythology.Model.entity

import com.example.app_mythology.Model.base.Entity

class Monster(
    name: String,
    mythology: String,
    val type: String,
    val description: String
) : Entity(name, mythology)
