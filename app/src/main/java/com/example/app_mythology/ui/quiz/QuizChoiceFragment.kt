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

class QuizChoiceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.bindPrimaryButtons(
            listOf(
                PrimaryButton("qcm", "QCM") {
                    findNavController().navigate(R.id.action_quizChoice_to_qcmDomain)
                },
                PrimaryButton("classique", "Classique") {
                    findNavController().navigate(R.id.action_quizChoice_to_classicDomain)
                },
                PrimaryButton("liste", "Liste") {
                    findNavController().navigate(R.id.action_quizChoice_to_quizListChoice)
                },
            )
        )

        view.findViewById<TextView>(R.id.btn_quiz_help).setOnClickListener {
            findNavController().navigate(R.id.action_quizChoice_to_quizHelp)
        }
    }
}
