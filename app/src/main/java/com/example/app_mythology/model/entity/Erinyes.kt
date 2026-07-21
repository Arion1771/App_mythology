package com.example.app_mythology.model.entity

import com.example.app_mythology.model.base.Entity

class Erinyes(
    name: String,
    mythology: String,
    val domain: String,
) : Entity(name, mythology)
