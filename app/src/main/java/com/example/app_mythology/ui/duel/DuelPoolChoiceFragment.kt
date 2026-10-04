package com.example.app_mythology.ui.duel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.app_mythology.R
import com.example.app_mythology.ui.common.setButtonAsset
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
        view.findViewById<ImageButton>(R.id.btn_duel_pool_same).apply {
            setButtonAsset("memes_questions", "Mêmes questions pour tous")
            setOnClickListener { start(true) }
        }
        view.findViewById<ImageButton>(R.id.btn_duel_pool_different).apply {
            setButtonAsset("questions_differentes", "Questions différentes pour chacun")
            setOnClickListener { start(false) }
        }
    }
}
