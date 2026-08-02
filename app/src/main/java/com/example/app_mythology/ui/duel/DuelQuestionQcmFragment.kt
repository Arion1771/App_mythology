package com.example.app_mythology.ui.duel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.app_mythology.R
import com.example.app_mythology.viewmodel.DuelViewModel

/** Question du duel en mode QCM : indice, 4 choix, un seul essai. */
class DuelQuestionQcmFragment : Fragment() {

    private val viewModel: DuelViewModel by navGraphViewModels(R.id.duel_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_duel_question_qcm, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvBadge     = view.findViewById<TextView>(R.id.tv_difficulty_badge)
        val tvHeader    = view.findViewById<TextView>(R.id.tv_duel_question_header)
        val tvMythology = view.findViewById<TextView>(R.id.tv_quiz_mythology)
        val tvRace      = view.findViewById<TextView>(R.id.tv_quiz_race)
        val tvClue      = view.findViewById<TextView>(R.id.tv_quiz_clue)
        val choiceButtons = listOf(
            view.findViewById<Button>(R.id.btn_qcm_choice_0),
            view.findViewById<Button>(R.id.btn_qcm_choice_1),
            view.findViewById<Button>(R.id.btn_qcm_choice_2),
            view.findViewById<Button>(R.id.btn_qcm_choice_3)
        )

        val question = viewModel.currentQuestion()
        val player = viewModel.currentPlayer()
        if (question == null || player == null) {
            findNavController().navigateUp()
            return
        }
        tvHeader.text = "${player.name} — question ${(viewModel.currentRound.value ?: 0) + 1}"
        tvMythology.text = "Mythologie : ${question.mythology}"
        tvRace.text = "Race : ${translateRace(question.race)}"
        tvClue.text = question.clue?.takeIf { it.isNotBlank() }?.let { "Indice : $it" } ?: "Indice : —"
        tvBadge.text = question.difficulty.toString()
        tvBadge.setBackgroundResource(difficultyBadgeRes(question.difficulty))

        viewModel.qcmChoices.observe(viewLifecycleOwner) { choices ->
            if (choices.size < 4) return@observe
            choiceButtons.forEachIndexed { i, btn ->
                btn.text = choices[i]
                btn.isEnabled = true
            }
        }

        choiceButtons.forEach { btn ->
            btn.setOnClickListener {
                choiceButtons.forEach { it.isEnabled = false }
                viewModel.submitQcmAnswer(btn.text.toString())
            }
        }

        viewModel.answerRevealed.observe(viewLifecycleOwner) { revealed ->
            if (revealed) findNavController().navigate(R.id.action_duelQuestionQcm_to_duelRecap)
        }
    }

    private fun difficultyBadgeRes(difficulty: Int) = when (difficulty) {
        1 -> R.drawable.dot_green
        2 -> R.drawable.dot_yellow
        3 -> R.drawable.dot_red
        else -> R.drawable.dot_neutral
    }

    private fun translateRace(race: String) = when (race) {
        "God"              -> "Dieu"
        "Titan"            -> "Titan"
        "Giant"            -> "Géant"
        "Heroes"           -> "Héros"
        "Monster"          -> "Monstre"
        "Cyclope"          -> "Cyclope"
        "Hecatoncheires"   -> "Hécatonchire"
        "Muses"            -> "Muse"
        "Erinyes"          -> "Érinye"
        "Grées"            -> "Grée"
        "Valkyrie"         -> "Valkyrie"
        "Archangels"       -> "Archange"
        "Arthurian_Knight" -> "Chevalier Arthurien"
        "Demon_Prince"     -> "Démon"
        "Zodiacal_Sign"    -> "Signe du Zodiaque"
        else               -> race
    }
}
