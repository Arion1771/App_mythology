package com.example.app_mythology.Model.place

import com.example.app_mythology.Model.base.Place

class Realm(
    name: String,
    mythology: String,
    description: String,
    val inhabitants: String
) : Place(name, mythology, description)
