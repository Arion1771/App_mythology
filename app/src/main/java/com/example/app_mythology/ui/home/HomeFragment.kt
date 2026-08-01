package com.example.app_mythology.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_browse).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_browseChoice)
        }

        view.findViewById<Button>(R.id.btn_add).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_addChoice)
        }

        view.findViewById<Button>(R.id.btn_quiz).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_quizChoice)
        }

        view.findViewById<View>(R.id.btn_trophy).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_achievements)
        }
    }
}
