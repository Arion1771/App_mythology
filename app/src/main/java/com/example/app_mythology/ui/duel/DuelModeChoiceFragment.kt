package com.example.app_mythology.ui.duel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.app_mythology.R
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
        view.findViewById<Button>(R.id.btn_duel_mode_classic).setOnClickListener { go(DuelViewModel.DuelMode.CLASSIC) }
        view.findViewById<Button>(R.id.btn_duel_mode_qcm).setOnClickListener { go(DuelViewModel.DuelMode.QCM) }
    }
}
