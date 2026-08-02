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
import kotlin.math.roundToInt

/**
 * Écran "base" d'un tour de duel : reste sur la pile pendant toute la partie
 * (question et récap y reviennent via popBackStack), donc son affichage est
 * piloté par observation des LiveData plutôt que par une liaison ponctuelle.
 */
class DuelAnnounceFragment : Fragment() {

    private val viewModel: DuelViewModel by navGraphViewModels(R.id.duel_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_duel_announce, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvPlayer = view.findViewById<TextView>(R.id.tv_duel_announce_player)
        val tvScore  = view.findViewById<TextView>(R.id.tv_duel_announce_score)
        val btnStart = view.findViewById<Button>(R.id.btn_duel_announce_start)

        fun refresh() {
            val player = viewModel.currentPlayer() ?: return
            val round = viewModel.currentRound.value ?: 0
            val total = viewModel.questionCount.value ?: 0
            tvPlayer.text = "Au tour de ${player.name}"
            tvScore.text = "Score actuel : ${formatScore(player.score)} — Question ${round + 1} / $total"
        }

        viewModel.currentPlayerIndex.observe(viewLifecycleOwner) { refresh() }
        viewModel.currentRound.observe(viewLifecycleOwner) { refresh() }
        viewModel.players.observe(viewLifecycleOwner) { refresh() }

        btnStart.setOnClickListener {
            val action = if (viewModel.mode == DuelViewModel.DuelMode.CLASSIC)
                R.id.action_duelAnnounce_to_duelQuestionClassic
            else
                R.id.action_duelAnnounce_to_duelQuestionQcm
            findNavController().navigate(action)
        }
    }

    private fun formatScore(v: Double): String =
        if (v == v.roundToInt().toDouble()) v.roundToInt().toString()
        else String.format("%.1f", v)
}
