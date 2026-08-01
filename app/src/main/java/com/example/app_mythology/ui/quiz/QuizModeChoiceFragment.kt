package com.example.app_mythology.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R

/**
 * Écran de choix entre le mode Classique et le mode QCM, réutilisé pour les
 * quiz d'entités et d'artéfacts. Argument "quizType" : "entity" | "artifact".
 */
class QuizModeChoiceFragment : Fragment() {

    private lateinit var quizType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizType = arguments?.getString("quizType") ?: "entity"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_mode_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tv_mode_choice_title).text =
            if (quizType == "artifact") "Quiz Artéfacts" else "Quiz Entités"

        view.findViewById<Button>(R.id.btn_mode_classic).setOnClickListener {
            findNavController().navigate(R.id.action_mode_to_classic)
        }
        view.findViewById<Button>(R.id.btn_mode_qcm).setOnClickListener {
            findNavController().navigate(R.id.action_mode_to_qcmChoice)
        }
    }
}
