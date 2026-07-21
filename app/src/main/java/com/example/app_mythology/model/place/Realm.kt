package com.example.app_mythology.model.place

import com.example.app_mythology.model.base.Place

class Realm(
    name: String,
    mythology: String,
    description: String,
    val inhabitants: String
) : Place(name, mythology, description)
