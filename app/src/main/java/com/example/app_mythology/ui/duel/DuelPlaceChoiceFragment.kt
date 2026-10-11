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

/** Choix du quiz Lieux joué en Duel : réutilise l'écran de choix du quiz Lieux solo. */
class DuelPlaceChoiceFragment : Fragment() {

    private val viewModel: DuelViewModel by navGraphViewModels(R.id.duel_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_place_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun start(type: String) {
            viewModel.startPlaceDuel(type)
            findNavController().navigate(R.id.action_duelPlaceChoice_to_duelPlace)
        }
        view.bindPrimaryButtons(
            listOf(
                PrimaryButton("arbre_monde", "Arbre Monde") { start("yggdrasil") },
                PrimaryButton("fleuves_enfer", "Fleuves de l'Enfer") { start("rivers") },
                PrimaryButton("royaume_morts", "Royaume des Morts") { start("underworld") },
            )
        )
    }
}
