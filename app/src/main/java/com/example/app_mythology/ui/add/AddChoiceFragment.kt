package com.example.app_mythology.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R

class AddChoiceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_add_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_add_entity).setOnClickListener {
            findNavController().navigate(R.id.action_addChoice_to_addEntity)
        }

        view.findViewById<Button>(R.id.btn_add_place).setOnClickListener {
            findNavController().navigate(R.id.action_addChoice_to_addPlace)
        }

        view.findViewById<Button>(R.id.btn_add_artifact).setOnClickListener {
            findNavController().navigate(R.id.action_addChoice_to_addArtifact)
        }
    }
}
