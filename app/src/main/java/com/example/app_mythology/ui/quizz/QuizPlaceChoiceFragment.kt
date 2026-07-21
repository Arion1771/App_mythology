package com.example.app_mythology.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R

class QuizPlaceChoiceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_place_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_quiz_yggdrasil).setOnClickListener {
            findNavController().navigate(R.id.action_quizPlaceChoice_to_quizYggdrasil)
        }
        view.findViewById<Button>(R.id.btn_quiz_rivers).setOnClickListener {
            findNavController().navigate(R.id.action_quizPlaceChoice_to_quizRivers)
        }
        view.findViewById<Button>(R.id.btn_quiz_underworld).setOnClickListener {
            findNavController().navigate(R.id.action_quizPlaceChoice_to_quizUnderworld)
        }
    }
}
