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
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.viewmodel.QuizViewModel

class QuizEntityResultFragment : Fragment() {

    private val viewModel: QuizViewModel by navGraphViewModels(R.id.quiz_entity_graph)

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

        val entities = viewModel.quizEntites.value ?: emptyList()
        val index    = viewModel.currentIndex.value ?: 0
        val entity   = entities.getOrNull(index)
        val result   = viewModel.results.value?.getOrNull(index)

        if (entity == null) {
            findNavController().popBackStack()
            return
        }

        val color = when (result) {
            "green"  -> Color.parseColor("#FF4CAF50")
            "yellow" -> Color.parseColor("#FFFFC107")
            else     -> Color.parseColor("#FFFF5252")
        }
        tvStatus.text = when (result) {
            "green"  -> "Bonne réponse !"
            "yellow" -> "Bonne réponse (au second essai)"
            else     -> "Mauvaise réponse"
        }
        tvStatus.setTextColor(color)
        tvName.text = entity.name
        tvName.setTextColor(color)
        tvInfo.text = buildAllInfo(entity)

        val isLast = index + 1 >= entities.size
        btnNext.text = if (isLast) "Voir le score" else "Question suivante →"
        btnNext.setOnClickListener {
            viewModel.nextAfterWrong()
            findNavController().popBackStack()
        }
    }

    private fun buildAllInfo(e: EntiteEntity) = buildString {
        e.domain?.let         { appendLine("Domaine : $it") }
        e.godType?.let        { appendLine("Type divin : ${translateGodType(it)}") }
        e.fatherName?.let     { appendLine("Père : $it") }
        e.motherName?.let     { appendLine("Mère : $it") }
        e.equivalentName?.let { appendLine("Équivalent : $it") }
        e.opponentName?.let   { appendLine("Opposant : $it") }
        e.giantType?.let      { appendLine("Type de géant : $it") }
        e.story?.let          { appendLine("Histoire : $it") }
        e.killer?.let         { appendLine("Tué par : $it") }
        e.role?.let           { appendLine("Rôle : $it") }
        e.death?.let          { appendLine("Mort : $it") }
        e.description?.let    { appendLine("Description : $it") }
        e.ascendantName?.let  { appendLine("Ascendant : $it") }
        e.monsterType?.let    { appendLine("Type : $it") }
        e.museType?.let       { appendLine("Type de muse : $it") }
        e.zodiacType?.let     { appendLine("Zodiaque : $it") }
        e.popularCulture?.let { appendLine("Culture populaire : $it") }
    }.trimEnd()

    private fun translateGodType(t: String) = when (t) {
        "Olympien" -> "Olympien"
        "Primordial", "Primodrial" -> "Primordial"
        "Ase" -> "Ase"; "Vane" -> "Vane"; "Norne" -> "Norne"
        "Parque" -> "Parque"; "Deva" -> "Deva"; "Yaksha" -> "Yaksha"
        "Asura" -> "Asura"; "Naga" -> "Naga"; "Rakshasa" -> "Rakshasa"
        "Humain" -> "Humain"; "Autre" -> "Autre"
        else -> t
    }
}
