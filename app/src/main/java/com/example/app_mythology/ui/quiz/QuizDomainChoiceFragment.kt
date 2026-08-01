package com.example.app_mythology.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R

/**
 * Écran de choix du domaine (Entités / Artéfacts / Lieux) une fois le type de
 * quiz choisi. Argument "quizMode" : "qcm" (pas de Lieux, aucune variante QCM
 * pour ce quiz) | "classic".
 */
class QuizDomainChoiceFragment : Fragment() {

    private lateinit var quizMode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizMode = arguments?.getString("quizMode") ?: "classic"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_domain_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tv_domain_choice_title).text =
            if (quizMode == "qcm") "QCM" else "Classique"

        val btnPlace = view.findViewById<Button>(R.id.btn_domain_place)
        btnPlace.isVisible = quizMode != "qcm"

        view.findViewById<Button>(R.id.btn_domain_entity).setOnClickListener {
            findNavController().navigate(R.id.action_domain_to_entity)
        }
        view.findViewById<Button>(R.id.btn_domain_artifact).setOnClickListener {
            findNavController().navigate(R.id.action_domain_to_artifact)
        }
        btnPlace.setOnClickListener {
            findNavController().navigate(R.id.action_domain_to_place)
        }
    }
}
