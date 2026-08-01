package com.example.app_mythology.ui.quiz

import android.graphics.Color
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
import com.example.app_mythology.database.ArtifactEntity
import com.example.app_mythology.viewmodel.QuizViewModel

class QuizArtifactQcmResultFragment : Fragment() {

    private val viewModel: QuizViewModel by navGraphViewModels(R.id.quiz_artifact_qcm_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_result, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvStatus = view.findViewById<TextView>(R.id.tv_result_status)
        val tvName   = view.findViewById<TextView>(R.id.tv_result_name)
        val tvInfo   = view.findViewById<TextView>(R.id.tv_result_info)
        val btnNext  = view.findViewById<Button>(R.id.btn_result_next)

        val artifacts = viewModel.qcmArtifacts.value ?: emptyList()
        val index     = viewModel.qcmIndex.value ?: 0
        val artifact  = artifacts.getOrNull(index)
        val result    = viewModel.qcmResults.value?.getOrNull(index)

        if (artifact == null) {
            findNavController().popBackStack()
            return
        }

        val color = if (result == "green") Color.parseColor("#FF4CAF50") else Color.parseColor("#FFFF5252")
        tvStatus.text = if (result == "green") "Bonne réponse !" else "Mauvaise réponse"
        tvStatus.setTextColor(color)
        tvName.text = artifact.name
        tvName.setTextColor(color)
        tvInfo.text = buildAllInfo(artifact)

        val isLast = index + 1 >= artifacts.size
        btnNext.text = if (isLast) "Voir le score" else "Question suivante →"
        btnNext.setOnClickListener {
            viewModel.nextQcmQuestion()
            findNavController().popBackStack()
        }
    }

    private fun buildAllInfo(a: ArtifactEntity) = buildString {
        a.ownerName?.let   { appendLine("Propriétaire : $it") }
        a.creatorName?.let { appendLine("Créateur : $it") }
        a.power?.let       { appendLine("Pouvoir : $it") }
        a.story?.let       { appendLine("Histoire : $it") }
        a.description?.let { appendLine("Description : $it") }
    }.trimEnd()
}
