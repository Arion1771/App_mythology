package com.example.app_mythology.Model.entity

import com.example.app_mythology.Model.base.Entity

class Arthurian_Knight(
    name: String,
    mythology: String,
    val story: String,
    val death: String? = null
) : Entity(name, mythology)
