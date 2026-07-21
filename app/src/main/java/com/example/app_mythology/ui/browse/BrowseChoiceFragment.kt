package com.example.app_mythology.ui.browse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R

class BrowseChoiceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_browse_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_browse_entity).setOnClickListener {
            findNavController().navigate(R.id.action_browseChoice_to_entityList)
        }

        view.findViewById<Button>(R.id.btn_browse_place).setOnClickListener {
            findNavController().navigate(R.id.action_browseChoice_to_placeList)
        }
    }
}
