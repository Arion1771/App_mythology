package com.example.app_mythology.ui.duel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.app_mythology.R
import com.example.app_mythology.ui.common.PrimaryButton
import com.example.app_mythology.ui.common.bindPrimaryButtons
import com.example.app_mythology.viewmodel.DuelViewModel

class DuelModeChoiceFragment : Fragment() {

    private val viewModel: DuelViewModel by navGraphViewModels(R.id.duel_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_duel_mode_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun go(mode: DuelViewModel.DuelMode) {
            viewModel.mode = mode
            findNavController().navigate(R.id.action_duelModeChoice_to_duelDifficultyChoice)
        }
        val buttons = mutableListOf(
            PrimaryButton("classique", "Classique") { go(DuelViewModel.DuelMode.CLASSIC) },
            PrimaryButton("qcm", "QCM") { go(DuelViewModel.DuelMode.QCM) },
        )
        // Duel Lieux : un essai chacun à tour de rôle, réservé aux parties à deux joueurs.
        if (viewModel.players.value?.size == 2) {
            buttons += PrimaryButton("places", "Lieux") {
                findNavController().navigate(R.id.action_duelModeChoice_to_duelPlaceChoice)
            }
        }
        view.bindPrimaryButtons(buttons)
    }
}
