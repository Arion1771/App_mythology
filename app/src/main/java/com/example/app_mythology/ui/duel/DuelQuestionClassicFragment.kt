package com.example.app_mythology.ui.duel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.app_mythology.R
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.viewmodel.DuelViewModel

/** Question du duel en mode Classique : indice, 2 essais, infos révélées après le 1er essai faux. */
class DuelQuestionClassicFragment : Fragment() {

    private val viewModel: DuelViewModel by navGraphViewModels(R.id.duel_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_duel_question_classic, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvBadge      = view.findViewById<TextView>(R.id.tv_difficulty_badge)
        val tvHeader     = view.findViewById<TextView>(R.id.tv_duel_question_header)
        val tvMythology  = view.findViewById<TextView>(R.id.tv_quiz_mythology)
        val tvDomain     = view.findViewById<TextView>(R.id.tv_quiz_domain)
        val tvRace       = view.findViewById<TextView>(R.id.tv_quiz_race)
        val tvAllInfo    = view.findViewById<TextView>(R.id.tv_quiz_all_info)
        val groupAllInfo = view.findViewById<View>(R.id.group_all_info)
        val etAnswer     = view.findViewById<EditText>(R.id.et_answer)
        val btnValidate  = view.findViewById<Button>(R.id.btn_validate)

        fun bind(e: EntiteEntity, step: Int) {
            val player = viewModel.currentPlayer()
            tvHeader.text = "${player?.name} — question ${(viewModel.currentRound.value ?: 0) + 1}"
            tvMythology.text = "Mythologie : ${e.mythology}"
            tvRace.text = "Race : ${translateRace(e.race)}"
            tvBadge.text = e.difficulty.toString()
            tvBadge.setBackgroundResource(difficultyBadgeRes(e.difficulty))
            tvDomain.text = e.clue?.takeIf { it.isNotBlank() }?.let { "Indice : $it" } ?: "Indice : —"
            etAnswer.text.clear()
            etAnswer.isEnabled = true
            btnValidate.isEnabled = true
            groupAllInfo.isVisible = step == 2
            if (step == 2) tvAllInfo.text = buildAllInfo(e)
        }

        viewModel.currentStep.observe(viewLifecycleOwner) { step ->
            val e = viewModel.currentQuestion() ?: return@observe
            bind(e, step)
        }

        viewModel.answerRevealed.observe(viewLifecycleOwner) { revealed ->
            if (revealed) {
                btnValidate.isEnabled = false
                etAnswer.isEnabled = false
                findNavController().navigate(R.id.action_duelQuestionClassic_to_duelRecap)
            }
        }

        btnValidate.setOnClickListener {
            val input = etAnswer.text.toString()
            if (input.isBlank()) return@setOnClickListener
            viewModel.submitClassicAnswer(viewModel.checkClassicAnswer(input))
        }
    }

    private fun difficultyBadgeRes(difficulty: Int) = when (difficulty) {
        1 -> R.drawable.dot_green
        2 -> R.drawable.dot_yellow
        3 -> R.drawable.dot_red
        else -> R.drawable.dot_neutral
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
        "Erinyes"          -> "Érinye"
        "Grées"            -> "Grée"
        "Valkyrie"         -> "Valkyrie"
        "Archangels"       -> "Archange"
        "Arthurian_Knight" -> "Chevalier Arthurien"
        "Demon_Prince"     -> "Démon"
        "Zodiacal_Sign"    -> "Signe du Zodiaque"
        else               -> race
    }

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
