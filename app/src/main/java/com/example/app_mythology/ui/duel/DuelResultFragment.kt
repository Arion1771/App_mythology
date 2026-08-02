package com.example.app_mythology.ui.duel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.app_mythology.R
import com.example.app_mythology.viewmodel.DuelViewModel
import kotlin.math.roundToInt

class DuelResultFragment : Fragment() {

    private val viewModel: DuelViewModel by navGraphViewModels(R.id.duel_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_duel_result, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = view.findViewById<LinearLayout>(R.id.container_ranking)
        val ranked = (viewModel.players.value ?: emptyList()).sortedByDescending { it.score }

        ranked.forEachIndexed { i, player ->
            val row = TextView(requireContext()).apply {
                text = "${i + 1}. ${player.name} — ${formatScore(player.score)} pts"
                textSize = 17f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface))
                setPadding(0, dpToPx(8), 0, dpToPx(8))
            }
            container.addView(row)
        }

        view.findViewById<Button>(R.id.btn_duel_result_home).setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }

    private fun formatScore(v: Double): String =
        if (v == v.roundToInt().toDouble()) v.roundToInt().toString()
        else String.format("%.1f", v)

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
}
