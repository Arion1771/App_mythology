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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_entity, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvProgress = view.findViewById<TextView>(R.id.tv_quiz_progress)
        val tvMythology = view.findViewById<TextView>(R.id.tv_quiz_mythology)
        val tvDomain = view.findViewById<TextView>(R.id.tv_quiz_domain)
        val tvRace = view.findViewById<TextView>(R.id.tv_quiz_race)
        val tvAllInfo = view.findViewById<TextView>(R.id.tv_quiz_all_info)  // Visible au pas 2
        val groupAllInfo = view.findViewById<View>(R.id.group_all_info)
        val etAnswer = view.findViewById<EditText>(R.id.et_answer)
        val btnValidate = view.findViewById<Button>(R.id.btn_validate)
        val tvScore = view.findViewById<TextView>(R.id.tv_score)
        val layoutResult = view.findViewById<View>(R.id.layout_result)

        viewModel.loadEntityQuiz()

        // Observer la question courante
        fun bindQuestion(entities: List<EntiteEntity>, index: Int, step: Int) {
            val entity = entities.getOrNull(index) ?: return
            tvProgress.text = "Question ${index + 1} / ${entities.size}"
            tvMythology.text = "Mythologie : ${entity.mythology}"
            tvDomain.text = "Domaine : ${entity.domain ?: "—"}"
            tvRace.text = "Race : ${entity.race}"
            etAnswer.text.clear()

            // Pas 1 : mytho + domaine seulement
            // Pas 2 : tout sauf le nom
            groupAllInfo.isVisible = step == 2
            if (step == 2) {
                tvAllInfo.text = buildAllInfo(entity)
            }
        }

        viewModel.quizEntites.observe(viewLifecycleOwner) { entities ->
            viewModel.currentIndex.observe(viewLifecycleOwner) { index ->
                viewModel.currentStep.observe(viewLifecycleOwner) { step ->
                    bindQuestion(entities, index, step)
                }
            }
        }

        btnValidate.setOnClickListener {
            val input = etAnswer.text.toString()
            val correct = viewModel.checkAnswer(input)
            viewModel.submitAnswer(correct)
        }

        viewModel.quizFinished.observe(viewLifecycleOwner) { finished ->
            if (finished) {
                layoutResult.isVisible = true
                btnValidate.isEnabled = false
                val score = viewModel.score.value ?: 0
                tvScore.text = "Score final : $score / 20"
            }
        }
    }

    private fun buildAllInfo(e: EntiteEntity): String {
        return buildString {
            appendLine("Race : ${e.race}")
            appendLine("Mythologie : ${e.mythology}")
            e.domain?.let { appendLine("Domaine : $it") }
            e.godType?.let { appendLine("Type : $it") }
            e.story?.let { appendLine("Histoire : $it") }
            e.killer?.let { appendLine("Tué par : $it") }
            e.role?.let { appendLine("Rôle : $it") }
            e.death?.let { appendLine("Mort : $it") }
            e.description?.let { appendLine("Description : $it") }
        }.trimEnd()
    }
}
