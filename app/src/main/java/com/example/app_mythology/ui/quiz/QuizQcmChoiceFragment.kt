package com.example.app_mythology.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R
import com.example.app_mythology.ui.common.setButtonAsset

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
                bundleOf("level" to level)
            )
        }
        view.findViewById<ImageButton>(R.id.btn_level_easy).apply {
            setButtonAsset("facile", "Facile (10 questions)")
            setOnClickListener { go("easy") }
        }
        view.findViewById<ImageButton>(R.id.btn_level_medium).apply {
            setButtonAsset("moyen", "Moyen (20 questions)")
            setOnClickListener { go("medium") }
        }
        view.findViewById<ImageButton>(R.id.btn_level_hard).apply {
            setButtonAsset("difficile", "Difficile (30 questions)")
            setOnClickListener { go("hard") }
        }
    }
}
