package com.example.app_mythology.ui.add

import android.os.Bundle

class EditPlaceFragment : AddPlaceFragment() {

    override val editMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        existingId = arguments?.getInt("placeId") ?: -1
    }
}
