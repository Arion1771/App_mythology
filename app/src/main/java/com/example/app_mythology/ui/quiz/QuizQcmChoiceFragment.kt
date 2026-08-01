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

/** Choix de la difficulté du QCM, réutilisé pour entités et artéfacts. Argument "quizType". */
class QuizQcmChoiceFragment : Fragment() {

    private lateinit var quizType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizType = arguments?.getString("quizType") ?: "entity"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_entity_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun go(level: String) {
            findNavController().navigate(
                R.id.action_qcmChoice_to_qcm,
                bundleOf("level" to level, "quizType" to quizType)
            )
        }
        view.findViewById<Button>(R.id.btn_level_easy).setOnClickListener { go("easy") }
        view.findViewById<Button>(R.id.btn_level_medium).setOnClickListener { go("medium") }
        view.findViewById<Button>(R.id.btn_level_hard).setOnClickListener { go("hard") }
    }
}
