package com.example.app_mythology.ui.quiz

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R
import com.example.app_mythology.viewmodel.QuizViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Quiz QCM (entités ou artéfacts) : 4 choix, un seul essai, passage immédiat
 * à la question suivante quel que soit le résultat. Arguments "quizType", "level".
 */
class QuizQcmFragment : Fragment() {

    private val viewModel: QuizViewModel by viewModels()
    private val dots = mutableListOf<View>()
    private lateinit var quizType: String
    private var answering = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizType = arguments?.getString("quizType") ?: "entity"
    }

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

        val tvBadge      = view.findViewById<TextView>(R.id.tv_difficulty_badge)
        val tvProgress   = view.findViewById<TextView>(R.id.tv_quiz_progress)
        val tvMythology  = view.findViewById<TextView>(R.id.tv_quiz_mythology)
        val tvRace       = view.findViewById<TextView>(R.id.tv_quiz_race)
        val tvClue       = view.findViewById<TextView>(R.id.tv_quiz_clue)
        val dotsContainer= view.findViewById<LinearLayout>(R.id.dots_container)
        val choiceButtons = listOf(
            view.findViewById<Button>(R.id.btn_qcm_choice_0),
            view.findViewById<Button>(R.id.btn_qcm_choice_1),
            view.findViewById<Button>(R.id.btn_qcm_choice_2),
            view.findViewById<Button>(R.id.btn_qcm_choice_3)
        )
        val layoutResult = view.findViewById<View>(R.id.layout_result)
        val tvScore      = view.findViewById<TextView>(R.id.tv_score)
        val btnRestart   = view.findViewById<Button>(R.id.btn_restart)

        if (quizType == "artifact") viewModel.loadArtifactQcm(level) else viewModel.loadEntityQcm(level)

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

        fun resetButtonColors() {
            choiceButtons.forEach {
                it.setBackgroundColor(Color.parseColor("#FFB36A"))
                it.isEnabled = true
            }
        }

        fun bindQuestion(index: Int) {
            val artifacts = viewModel.qcmArtifacts.value
            val name: String; val mythology: String; val race: String?; val clue: String?; val difficulty: Int
            if (quizType == "artifact" && !artifacts.isNullOrEmpty()) {
                val a = artifacts.getOrNull(index) ?: return
                name = a.name; mythology = a.mythology; race = a.artifactType
                clue = a.clue; difficulty = a.difficulty
                tvProgress.text = "Question ${index + 1} / ${artifacts.size}"
            } else {
                val entites = viewModel.qcmEntites.value ?: return
                val e = entites.getOrNull(index) ?: return
                name = e.name; mythology = e.mythology; race = translateRace(e.race)
                clue = e.clue; difficulty = e.difficulty
                tvProgress.text = "Question ${index + 1} / ${entites.size}"
            }
            tvMythology.text = "Mythologie : $mythology"
            tvRace.text = "Race : ${race ?: "—"}"
            tvClue.text = clue?.takeIf { it.isNotBlank() }?.let { "Indice : $it" } ?: "Indice : —"
            tvBadge.text = difficulty.toString()
            tvBadge.setBackgroundResource(difficultyBadgeRes(difficulty))
        }

        viewModel.qcmChoices.observe(viewLifecycleOwner) { choices ->
            if (choices.size < 4) return@observe
            answering = true
            resetButtonColors()
            choiceButtons.forEachIndexed { i, btn -> btn.text = choices[i] }
            val index = viewModel.qcmIndex.value ?: 0
            bindQuestion(index)
        }

        viewModel.qcmEntites.observe(viewLifecycleOwner) { list -> rebuildDots(list.size) }
        viewModel.qcmArtifacts.observe(viewLifecycleOwner) { list -> if (list.isNotEmpty()) rebuildDots(list.size) }

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
                if (!answering) return@setOnClickListener
                answering = false
                choiceButtons.forEach { it.isEnabled = false }
                viewModel.submitQcmAnswer(btn.text.toString())
            }
        }

        viewModel.qcmFeedback.observe(viewLifecycleOwner) { feedback ->
            if (feedback == null) return@observe
            val (selected, correct) = feedback
            choiceButtons.forEach { btn ->
                when {
                    btn.text.toString() == selected && correct -> btn.setBackgroundColor(Color.parseColor("#FF4CAF50"))
                    btn.text.toString() == selected && !correct -> btn.setBackgroundColor(Color.parseColor("#FFFF5252"))
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                delay(900)
                if (viewModel.qcmFeedback.value != null) viewModel.advanceQcm()
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
