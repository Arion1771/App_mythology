package com.example.app_mythology.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.app_mythology.R
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.viewmodel.QuizViewModel
import kotlin.math.roundToInt

/** Quiz QCM Entités : 4 choix, un seul essai, écran de résultat par question. */
class QuizEntityQcmFragment : Fragment() {

    private val viewModel: QuizViewModel by navGraphViewModels(R.id.quiz_entity_qcm_graph)
    private val dots = mutableListOf<View>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_qcm, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val level = when (arguments?.getString("level")) {
            "medium" -> QuizViewModel.QuizLevel.MEDIUM
            "hard"   -> QuizViewModel.QuizLevel.HARD
            else     -> QuizViewModel.QuizLevel.EASY
        }

        val tvBadge       = view.findViewById<TextView>(R.id.tv_difficulty_badge)
        val tvProgress    = view.findViewById<TextView>(R.id.tv_quiz_progress)
        val tvMythology   = view.findViewById<TextView>(R.id.tv_quiz_mythology)
        val tvRace        = view.findViewById<TextView>(R.id.tv_quiz_race)
        val tvClue        = view.findViewById<TextView>(R.id.tv_quiz_clue)
        val dotsContainer = view.findViewById<LinearLayout>(R.id.dots_container)
        val choiceButtons = listOf(
            view.findViewById<Button>(R.id.btn_qcm_choice_0),
            view.findViewById<Button>(R.id.btn_qcm_choice_1),
            view.findViewById<Button>(R.id.btn_qcm_choice_2),
            view.findViewById<Button>(R.id.btn_qcm_choice_3)
        )
        val layoutResult = view.findViewById<View>(R.id.layout_result)
        val tvScore       = view.findViewById<TextView>(R.id.tv_score)
        val btnRestart    = view.findViewById<Button>(R.id.btn_restart)

        viewModel.loadEntityQcm(level)

        fun rebuildDots(count: Int) {
            dots.clear()
            dotsContainer.removeAllViews()
            repeat(count) {
                val dot = View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(22, 22).also { p -> p.marginEnd = 6 }
                    setBackgroundResource(R.drawable.dot_neutral)
                }
                dotsContainer.addView(dot)
                dots.add(dot)
            }
        }

        fun bindQuestion(entites: List<EntiteEntity>, index: Int) {
            val e = entites.getOrNull(index) ?: return
            tvProgress.text  = "Question ${index + 1} / ${entites.size}"
            tvMythology.text = "Mythologie : ${e.mythology}"
            tvRace.text      = "Race : ${translateRace(e.race)}"
            tvClue.text      = e.clue?.takeIf { it.isNotBlank() }?.let { "Indice : $it" } ?: "Indice : —"
            tvBadge.text     = e.difficulty.toString()
            tvBadge.setBackgroundResource(difficultyBadgeRes(e.difficulty))
        }

        viewModel.qcmEntites.observe(viewLifecycleOwner) { entites ->
            rebuildDots(entites.size)
            viewModel.qcmIndex.observe(viewLifecycleOwner) { index ->
                if (viewModel.qcmFinished.value != true) bindQuestion(entites, index)
            }
        }

        viewModel.qcmChoices.observe(viewLifecycleOwner) { choices ->
            if (choices.size < 4) return@observe
            choiceButtons.forEachIndexed { i, btn ->
                btn.text = choices[i]
                btn.isEnabled = true
                btn.isVisible = true
            }
        }

        viewModel.qcmResults.observe(viewLifecycleOwner) { results ->
            results.forEachIndexed { i, result ->
                if (i < dots.size) {
                    dots[i].setBackgroundResource(when (result) {
                        "green" -> R.drawable.dot_green
                        "red"   -> R.drawable.dot_red
                        else    -> R.drawable.dot_neutral
                    })
                }
            }
        }

        choiceButtons.forEach { btn ->
            btn.setOnClickListener {
                choiceButtons.forEach { it.isEnabled = false }
                viewModel.submitQcmAnswer(btn.text.toString())
            }
        }

        viewModel.qcmAnswerRevealed.observe(viewLifecycleOwner) { revealed ->
            if (revealed) {
                findNavController().navigate(R.id.action_qcmEntity_to_qcmEntityResult)
            }
        }

        viewModel.qcmFinished.observe(viewLifecycleOwner) { finished ->
            if (finished) {
                layoutResult.isVisible = true
                choiceButtons.forEach { it.isVisible = false }
                val score = viewModel.qcmScore.value ?: 0.0
                val max   = viewModel.qcmMaxScore.value ?: 0.0
                tvScore.text = "Score : ${formatScore(score)} / ${formatScore(max)}"
            }
        }

        btnRestart.setOnClickListener { findNavController().navigateUp() }
    }

    private fun formatScore(v: Double): String =
        if (v == v.roundToInt().toDouble()) v.roundToInt().toString()
        else String.format("%.1f", v)

    private fun difficultyBadgeRes(difficulty: Int) = when (difficulty) {
        1    -> R.drawable.dot_green
        2    -> R.drawable.dot_yellow
        3    -> R.drawable.dot_red
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
