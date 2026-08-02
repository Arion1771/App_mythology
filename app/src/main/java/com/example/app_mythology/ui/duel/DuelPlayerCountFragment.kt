package com.example.app_mythology.ui.duel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R

/** Écran d'entrée du mode Duel : choix du nombre de joueurs (2 à 12), avec l'aide dédiée. */
class DuelPlayerCountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_duel_player_count, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val picker = view.findViewById<NumberPicker>(R.id.number_picker_players).apply {
            minValue = 2
            maxValue = 12
            value = 2
            wrapSelectorWheel = false
        }

        view.findViewById<Button>(R.id.btn_duel_players_next).setOnClickListener {
            findNavController().navigate(
                R.id.action_duelPlayerCount_to_duelPlayerNames,
                bundleOf("playerCount" to picker.value)
            )
        }

        view.findViewById<View>(R.id.btn_duel_help).setOnClickListener {
            findNavController().navigate(R.id.action_duelPlayerCount_to_duelHelp)
        }
    }
}
