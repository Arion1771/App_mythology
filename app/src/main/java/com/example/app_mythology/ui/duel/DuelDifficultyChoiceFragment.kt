package com.example.app_mythology.ui.duel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.app_mythology.R
import com.example.app_mythology.ui.common.setButtonAsset
import com.example.app_mythology.viewmodel.DuelViewModel
import com.example.app_mythology.viewmodel.QuizViewModel

/** Réutilise le layout de choix de difficulté du quiz solo (fragment_quiz_entity_choice.xml). */
class DuelDifficultyChoiceFragment : Fragment() {

    private val viewModel: DuelViewModel by navGraphViewModels(R.id.duel_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_entity_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun go(level: QuizViewModel.QuizLevel) {
            viewModel.level = level
            findNavController().navigate(R.id.action_duelDifficultyChoice_to_duelPoolChoice)
        }
        view.findViewById<ImageButton>(R.id.btn_level_easy).apply {
            setButtonAsset("facile", "Facile (10 questions)")
            setOnClickListener { go(QuizViewModel.QuizLevel.EASY) }
        }
        view.findViewById<ImageButton>(R.id.btn_level_medium).apply {
            setButtonAsset("moyen", "Moyen (20 questions)")
            setOnClickListener { go(QuizViewModel.QuizLevel.MEDIUM) }
        }
        view.findViewById<ImageButton>(R.id.btn_level_hard).apply {
            setButtonAsset("difficile", "Difficile (30 questions)")
            setOnClickListener { go(QuizViewModel.QuizLevel.HARD) }
        }
    }
}
