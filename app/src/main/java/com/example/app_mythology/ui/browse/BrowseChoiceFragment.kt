package com.example.app_mythology.ui.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R
import com.example.app_mythology.ui.common.PrimaryButton
import com.example.app_mythology.ui.common.bindPrimaryButtons

class BrowseChoiceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_browse_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.bindPrimaryButtons(
            listOf(
                PrimaryButton("entities", "Entités") {
                    findNavController().navigate(R.id.action_browseChoice_to_entityList)
                },
                PrimaryButton("places", "Lieux") {
                    findNavController().navigate(R.id.action_browseChoice_to_placeList)
                },
                PrimaryButton("artifacts", "Artéfacts") {
                    findNavController().navigate(R.id.action_browseChoice_to_artifactList)
                },
            )
        )
    }
}
