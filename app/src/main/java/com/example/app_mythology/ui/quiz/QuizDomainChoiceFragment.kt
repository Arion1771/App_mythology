package com.example.app_mythology.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R
import com.example.app_mythology.ui.common.PrimaryButton
import com.example.app_mythology.ui.common.bindPrimaryButtons

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

        val buttons = mutableListOf(
            PrimaryButton("entities", "Entités") {
                findNavController().navigate(R.id.action_domain_to_entity)
            },
            PrimaryButton("artifacts", "Artéfacts") {
                findNavController().navigate(R.id.action_domain_to_artifact)
            },
        )
        if (quizMode != "qcm") {
            buttons += PrimaryButton("places", "Lieux") {
                findNavController().navigate(R.id.action_domain_to_place)
            }
        }
        view.bindPrimaryButtons(buttons)
    }
}
