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
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.viewmodel.QuizViewModel

class QuizEntityFragment : Fragment() {

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

        viewModel.loadEntityQuiz(level)

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

        fun bindQuestion(entities: List<EntiteEntity>, index: Int, step: Int) {
            val e = entities.getOrNull(index) ?: return
            tvProgress.text  = "Question ${index + 1} / ${entities.size}"
            tvMythology.text = "Mythologie : ${e.mythology}"
            tvRace.text      = "Race : ${translateRace(e.race)}"

            // Seul l'indice (clue) est affiché — rien d'autre
            tvDomain.text = e.clue?.takeIf { it.isNotBlank() }?.let { "Indice : $it" } ?: "Indice : —"

            etAnswer.text.clear()
            etAnswer.isEnabled    = true
            btnValidate.isEnabled = true
            groupAllInfo.isVisible  = step == 2
            tvCorrectName.isVisible = false
            btnNext.isVisible       = false
            if (step == 2) tvAllInfo.text = buildAllInfo(e)
        }

        viewModel.quizEntites.observe(viewLifecycleOwner) { entities ->
            rebuildDots(entities.size)
            viewModel.currentIndex.observe(viewLifecycleOwner) { index ->
                viewModel.currentStep.observe(viewLifecycleOwner) { step ->
                    if (viewModel.quizFinished.value != true)
                        bindQuestion(entities, index, step)
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

        viewModel.waitingForNext.observe(viewLifecycleOwner) { waiting ->
            if (waiting) {
                val entity = viewModel.quizEntites.value
                    ?.getOrNull(viewModel.currentIndex.value ?: 0)
                tvCorrectName.text      = "Réponse : ${entity?.name}"
                tvCorrectName.isVisible = true
                btnValidate.isEnabled   = false
                etAnswer.isEnabled      = false
                btnNext.isVisible       = true
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
                btnValidate.isEnabled  = false
                etAnswer.isEnabled     = false
                btnNext.isVisible      = false
                val max = viewModel.maxScore.value ?: 20
                tvScore.text = "Score : ${viewModel.score.value ?: 0} / $max"
            }
        }

        btnRestart.setOnClickListener { findNavController().navigateUp() }
    }

    private fun buildAllInfo(e: EntiteEntity) = buildString {
        appendLine("Race : ${translateRace(e.race)}")
        appendLine("Mythologie : ${e.mythology}")
        e.clue?.let          { appendLine("Indice : $it") }
        e.domain?.let        { appendLine("Domaine : $it") }
        e.godType?.let       { appendLine("Type divin : ${translateGodType(it)}") }
        e.opponentName?.let  { appendLine("Opposant : $it") }
        e.giantType?.let     { appendLine("Type de géant : $it") }
        e.story?.let         { appendLine("Histoire : $it") }
        e.killer?.let        { appendLine("Tué par : $it") }
        e.role?.let          { appendLine("Rôle : $it") }
        e.death?.let         { appendLine("Mort : $it") }
        e.description?.let   { appendLine("Description : $it") }
        e.ascendantName?.let { appendLine("Ascendant : $it") }
        e.equivalentName?.let{ appendLine("Équivalent : $it") }
        e.monsterType?.let   { appendLine("Type : $it") }
        e.museType?.let      { appendLine("Type de muse : $it") }
        e.zodiacType?.let    { appendLine("Zodiaque : $it") }
    }.trimEnd()

    private fun translateRace(race: String) = when (race) {
        "God"              -> "Dieu"
        "Titan"            -> "Titan"
        "Giant"            -> "Géant"
        "Heroes"           -> "Héros"
        "Monster"          -> "Monstre"
        "Cyclope"          -> "Cyclope"
        "Hecatoncheires"   -> "Hécatonchire"
        "Muses"            -> "Muse"
        "Archangels"       -> "Archange"
        "Arthurian_Knight" -> "Chevalier Arthurien"
        "Demon_Prince"     -> "Démon"
        "Zodiacal_Sign"    -> "Signe du Zodiaque"
        else               -> race
    }

    private fun translateGodType(t: String) = when (t) {
        "Olympien" -> "Olympien"; "Primordial" -> "Primordial"
        "Ase" -> "Ase"; "Vane" -> "Vane"; "Norne" -> "Norne"
        "Parque" -> "Parque"; "Deva" -> "Deva"; "Yaksha" -> "Yaksha"
        "Asura" -> "Asura"; "Naga" -> "Naga"; "Rakshasa" -> "Rakshasa"
        "Humain" -> "Humain"; "Autre" -> "Autre"
        else -> t
    }
}
