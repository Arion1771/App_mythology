package com.example.app_mythology.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R

class QuizEntityChoiceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_entity_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_level_easy).setOnClickListener {
            findNavController().navigate(
                R.id.action_quizEntityChoice_to_quizEntity,
                bundleOf("level" to "easy")
            )
        }
        view.findViewById<Button>(R.id.btn_level_medium).setOnClickListener {
            findNavController().navigate(
                R.id.action_quizEntityChoice_to_quizEntity,
                bundleOf("level" to "medium")
            )
        }
        view.findViewById<Button>(R.id.btn_level_hard).setOnClickListener {
            findNavController().navigate(
                R.id.action_quizEntityChoice_to_quizEntity,
                bundleOf("level" to "hard")
            )
        }
    }
}
