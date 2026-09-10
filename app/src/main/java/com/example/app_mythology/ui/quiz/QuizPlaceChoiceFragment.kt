package com.example.app_mythology.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R
import com.example.app_mythology.ui.common.PrimaryButton
import com.example.app_mythology.ui.common.bindPrimaryButtons

class QuizPlaceChoiceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_place_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.bindPrimaryButtons(
            listOf(
                PrimaryButton("arbre_monde", "Arbre Monde") {
                    findNavController().navigate(R.id.action_quizPlaceChoice_to_quizYggdrasil)
                },
                PrimaryButton("fleuves_enfer", "Fleuves de l'Enfer") {
                    findNavController().navigate(R.id.action_quizPlaceChoice_to_quizRivers)
                },
                PrimaryButton("royaume_morts", "Royaume des Morts") {
                    findNavController().navigate(R.id.action_quizPlaceChoice_to_quizUnderworld)
                },
            )
        )
    }
}
