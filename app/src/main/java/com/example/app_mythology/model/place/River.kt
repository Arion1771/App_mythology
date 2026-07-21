package com.example.app_mythology.model.place

import com.example.app_mythology.model.base.Place

class River(
    name: String,
    mythology: String,
    description: String,
    val particularity: String
) : Place(name, mythology, description)
