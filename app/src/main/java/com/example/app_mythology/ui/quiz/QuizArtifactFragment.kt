package com.example.app_mythology.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R
import com.example.app_mythology.database.ArtifactEntity
import com.example.app_mythology.viewmodel.QuizViewModel
import kotlin.math.roundToInt

class QuizArtifactFragment : Fragment() {

    private val viewModel: QuizViewModel by viewModels()
    private val dots = mutableListOf<View>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_entity, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val level = when (arguments?.getString("level")) {
            "medium" -> QuizViewModel.QuizLevel.MEDIUM
            "hard"   -> QuizViewModel.QuizLevel.HARD
            else     -> QuizViewModel.QuizLevel.EASY
        }

        val tvDifficultyBadge = view.findViewById<TextView>(R.id.tv_difficulty_badge)
        val tvProgress    = view.findViewById<TextView>(R.id.tv_quiz_progress)
        val tvMythology   = view.findViewById<TextView>(R.id.tv_quiz_mythology)
        val tvDomain      = view.findViewById<TextView>(R.id.tv_quiz_domain)
        val tvRace        = view.findViewById<TextView>(R.id.tv_quiz_race)
        val tvAllInfo     = view.findViewById<TextView>(R.id.tv_quiz_all_info)
        val groupAllInfo  = view.findViewById<View>(R.id.group_all_info)
        val tvCorrectName = view.findViewById<TextView>(R.id.tv_correct_name)
        val etAnswer      = view.findViewById<EditText>(R.id.et_answer)
        val btnValidate   = view.findViewById<Button>(R.id.btn_validate)
        val btnNext       = view.findViewById<Button>(R.id.btn_next_after_wrong)
        val tvScore       = view.findViewById<TextView>(R.id.tv_score)
        val layoutResult  = view.findViewById<View>(R.id.layout_result)
        val btnRestart    = view.findViewById<Button>(R.id.btn_restart)
        val dotsContainer = view.findViewById<LinearLayout>(R.id.dots_container)

        viewModel.loadArtifactQuiz(level)

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

        fun bindQuestion(artifacts: List<ArtifactEntity>, index: Int, step: Int) {
            val a = artifacts.getOrNull(index) ?: return
            tvProgress.text  = "Question ${index + 1} / ${artifacts.size}"
            tvMythology.text = "Mythologie : ${a.mythology}"
            tvRace.text      = "Type : ${a.artifactType}"
            tvDifficultyBadge.text = a.difficulty.toString()

            tvDomain.text = a.clue?.takeIf { it.isNotBlank() }?.let { "Indice : $it" } ?: "Indice : —"

            etAnswer.text.clear()
            etAnswer.isEnabled      = true
            btnValidate.isEnabled   = true
            btnValidate.isVisible   = true
            groupAllInfo.isVisible  = step == 2
            tvCorrectName.isVisible = false
            btnNext.isVisible       = false
            if (step == 2) tvAllInfo.text = buildAllInfo(a)
        }

        viewModel.quizArtifacts.observe(viewLifecycleOwner) { artifacts ->
            rebuildDots(artifacts.size)
            viewModel.currentIndex.observe(viewLifecycleOwner) { index ->
                viewModel.currentStep.observe(viewLifecycleOwner) { step ->
                    if (viewModel.quizFinished.value != true)
                        bindQuestion(artifacts, index, step)
                }
            }
        }

        viewModel.results.observe(viewLifecycleOwner) { results ->
            results.forEachIndexed { i, result ->
                if (i < dots.size) {
                    dots[i].setBackgroundResource(when (result) {
                        "green"  -> R.drawable.dot_green
                        "yellow" -> R.drawable.dot_yellow
                        "red"    -> R.drawable.dot_red
                        else     -> R.drawable.dot_neutral
                    })
                }
            }
        }

        viewModel.answerRevealed.observe(viewLifecycleOwner) { revealed ->
            if (revealed) {
                btnValidate.isVisible = false
                etAnswer.isEnabled    = false
                btnNext.isVisible     = true

                val artifacts = viewModel.quizArtifacts.value ?: return@observe
                val index     = viewModel.currentIndex.value ?: 0
                val results   = viewModel.results.value ?: emptyList()
                val artifact  = artifacts.getOrNull(index) ?: return@observe
                val resultForThis = results.getOrNull(index)

                if (resultForThis == "green" || resultForThis == "yellow") {
                    groupAllInfo.isVisible  = true
                    tvAllInfo.text          = buildAllInfo(artifact)
                    tvCorrectName.isVisible = false
                } else if (resultForThis == "red") {
                    tvCorrectName.text      = "Réponse : ${artifact.name}"
                    tvCorrectName.isVisible = true
                }
            }
        }

        btnNext.setOnClickListener { viewModel.nextAfterWrong() }

        btnValidate.setOnClickListener {
            val input = etAnswer.text.toString()
            if (input.isBlank()) return@setOnClickListener
            viewModel.submitAnswer(viewModel.checkAnswer(input))
        }

        viewModel.quizFinished.observe(viewLifecycleOwner) { finished ->
            if (finished) {
                layoutResult.isVisible = true
                btnValidate.isVisible  = false
                etAnswer.isEnabled     = false
                btnNext.isVisible      = false
                val score = viewModel.score.value ?: 0.0
                val max   = viewModel.maxScore.value ?: 0.0
                tvScore.text = "Score : ${formatScore(score)} / ${formatScore(max)}"
            }
        }

        btnRestart.setOnClickListener { findNavController().navigateUp() }
    }

    private fun formatScore(v: Double): String =
        if (v == v.roundToInt().toDouble()) v.roundToInt().toString()
        else String.format("%.1f", v)

    private fun buildAllInfo(a: ArtifactEntity) = buildString {
        appendLine("Type : ${a.artifactType}")
        appendLine("Mythologie : ${a.mythology}")
        a.clue?.let        { appendLine("Indice : $it") }
        a.ownerName?.let   { appendLine("Propriétaire : $it") }
        a.creatorName?.let { appendLine("Créateur : $it") }
        a.power?.let       { appendLine("Pouvoir : $it") }
        a.story?.let       { appendLine("Histoire : $it") }
        a.description?.let { appendLine("Description : $it") }
    }.trimEnd()
}
