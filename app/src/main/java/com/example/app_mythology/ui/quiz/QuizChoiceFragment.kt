package com.example.app_mythology.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R

class QuizChoiceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_quiz_entity).setOnClickListener {
            findNavController().navigate(R.id.action_quizChoice_to_quizEntity)
        }

        view.findViewById<Button>(R.id.btn_quiz_place).setOnClickListener {
            findNavController().navigate(R.id.action_quizChoice_to_quizPlaceChoice)
        }

        view.findViewById<Button>(R.id.btn_quiz_artifact).setOnClickListener {
            findNavController().navigate(R.id.action_quizChoice_to_quizArtifactChoice)
        }
    }
}
