package com.example.app_mythology.Model.place

import com.example.app_mythology.Model.base.Place

class River(
    name: String,
    mythology: String,
    description: String,
    val particularity: String
) : Place(name, mythology, description)
