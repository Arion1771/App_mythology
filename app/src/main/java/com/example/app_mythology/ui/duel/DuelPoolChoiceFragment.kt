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

class DuelPoolChoiceFragment : Fragment() {

    private val viewModel: DuelViewModel by navGraphViewModels(R.id.duel_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_duel_pool_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun start(same: Boolean) {
            viewModel.sameQuestions = same
            viewModel.startDuel()
            findNavController().navigate(R.id.action_duelPoolChoice_to_duelAnnounce)
        }
        view.findViewById<Button>(R.id.btn_duel_pool_same).setOnClickListener { start(true) }
        view.findViewById<Button>(R.id.btn_duel_pool_different).setOnClickListener { start(false) }
    }
}
