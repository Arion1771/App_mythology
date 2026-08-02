package com.example.app_mythology.ui.duel

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.app_mythology.R
import com.example.app_mythology.viewmodel.DuelViewModel

/** Génère un champ de nom par joueur (nombre déjà choisi sur l'écran précédent). */
class DuelPlayerNamesFragment : Fragment() {

    private val viewModel: DuelViewModel by navGraphViewModels(R.id.duel_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_duel_player_names, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playerCount = arguments?.getInt("playerCount") ?: 2
        val container = view.findViewById<LinearLayout>(R.id.container_names)

        val fields = (1..playerCount).map { i ->
            EditText(requireContext()).apply {
                hint = "Joueur $i"
                setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface))
                setHintTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface_variant))
                backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primary))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = dpToPx(12) }
            }.also { container.addView(it) }
        }

        view.findViewById<Button>(R.id.btn_duel_names_next).setOnClickListener {
            val names = fields.mapIndexed { i, field ->
                field.text.toString().trim().ifBlank { "Joueur ${i + 1}" }
            }
            viewModel.setPlayerNames(names)
            findNavController().navigate(R.id.action_duelPlayerNames_to_duelModeChoice)
        }
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
}
