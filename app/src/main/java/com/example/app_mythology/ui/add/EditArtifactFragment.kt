package com.example.app_mythology.ui.add

import android.os.Bundle

class EditArtifactFragment : AddArtifactFragment() {

    override val editMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        existingId = arguments?.getInt("artifactId") ?: -1
    }
}
