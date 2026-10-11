package com.example.app_mythology.ui.duel

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.app_mythology.R
import com.example.app_mythology.database.PlaceEntity
import com.example.app_mythology.viewmodel.DuelViewModel

/**
 * Duel Lieux à deux joueurs, sur un seul écran : même grille de cartes que le
 * quiz Lieux solo, un essai chacun à tour de rôle (nom du joueur courant
 * affiché au-dessus du champ de saisie), 3 erreurs maximum par joueur.
 * Chaque joueur a sa couleur : celle de son nom quand c'est son tour, et
 * celle du nom des lieux qu'il a trouvés.
 */
class DuelPlaceFragment : Fragment() {

    private val viewModel: DuelViewModel by navGraphViewModels(R.id.duel_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_duel_place, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle      = view.findViewById<TextView>(R.id.tv_place_quiz_title)
        val container    = view.findViewById<GridLayout>(R.id.grid_places)
        val tvFoundCount = view.findViewById<TextView>(R.id.tv_found_count)
        val tvErrors     = view.findViewById<TextView>(R.id.tv_duel_place_errors)
        val tvPlayer     = view.findViewById<TextView>(R.id.tv_duel_place_player)
        val etInput      = view.findViewById<EditText>(R.id.et_place_answer)
        val btnValidate  = view.findViewById<Button>(R.id.btn_place_validate)
        val layoutInput  = view.findViewById<View>(R.id.layout_place_input)
        val layoutResult = view.findViewById<View>(R.id.layout_place_result)
        val tvScore      = view.findViewById<TextView>(R.id.tv_place_score)
        val btnRanking   = view.findViewById<Button>(R.id.btn_duel_place_ranking)

        val quizType = viewModel.placeQuizType
        tvTitle.text = when (quizType) {
            "yggdrasil" -> "Duel — Arbre Monde"
            "rivers" -> "Duel — Fleuves de l'Enfer"
            else -> "Duel — Le Royaume des Morts"
        }

        val players = viewModel.players.value ?: emptyList()
        var places = listOf<PlaceEntity>()

        fun playerName(index: Int) = players.getOrNull(index)?.name ?: "Joueur ${index + 1}"

        val playerColors = listOf(R.color.duel_player_1, R.color.duel_player_2)
            .map { ContextCompat.getColor(requireContext(), it) }
        fun playerColor(index: Int) = playerColors[index % playerColors.size]

        /** Une ligne "<nom> : <texte>" par joueur, le nom à la couleur du joueur. */
        fun coloredLines(separator: String, line: (Int) -> String): CharSequence {
            val out = SpannableStringBuilder()
            players.indices.forEach { i ->
                if (i > 0) out.append(separator)
                val start = out.length
                out.append(playerName(i))
                out.setSpan(ForegroundColorSpan(playerColor(i)), start, out.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                out.append(" : ").append(line(i))
            }
            return out
        }

        fun updateFoundCount() {
            val found = viewModel.placeFoundBy.value ?: emptyMap()
            tvFoundCount.text = "${found.size} / ${places.size} trouvés"
        }

        /** Carte trouvée : nom du lieu, à la couleur du joueur qui l'a trouvé ; en fin de partie, les autres sont révélées en rouge. */
        fun refreshCards() {
            val foundBy = viewModel.placeFoundBy.value ?: emptyMap()
            val finished = viewModel.placeFinished.value == true
            for (i in 0 until container.childCount) {
                val card = container.getChildAt(i)
                val id = card.tag as? Int ?: continue
                val place = places.firstOrNull { it.id == id } ?: continue
                val finder = foundBy[id]
                if (finder == null && !finished) continue
                card.findViewById<TextView>(R.id.tv_card_info).isVisible = false
                card.findViewById<TextView>(R.id.tv_card_name).apply {
                    text = place.name
                    if (finder != null) setTextColor(playerColor(finder))
                    isVisible = true
                }
                // Fond sombre pour les cartes trouvées : la couleur du joueur reste lisible.
                card.setBackgroundResource(if (finder != null) R.drawable.card_bg else R.drawable.card_notfound_bg)
            }
        }

        viewModel.placesOf(quizType).observe(viewLifecycleOwner) { list ->
            places = list
            container.removeAllViews()
            container.columnCount = 3
            list.forEach { place ->
                val card = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_place_card, container, false)
                val tvCardInfo = card.findViewById<TextView>(R.id.tv_card_info)
                card.tag = place.id
                // Clic sur une carte grisée → afficher l'info sans révéler le nom
                card.setOnClickListener {
                    if (place.id !in (viewModel.placeFoundBy.value ?: emptyMap())) {
                        tvCardInfo.text = when (quizType) {
                            "yggdrasil" -> place.inhabitants ?: "—"
                            "rivers" -> place.particularity ?: "—"
                            else -> place.description
                        }
                        tvCardInfo.isVisible = true
                    }
                }
                container.addView(card)
            }
            updateFoundCount()
            refreshCards()
        }

        viewModel.placeFoundBy.observe(viewLifecycleOwner) {
            refreshCards()
            updateFoundCount()
        }

        viewModel.placeErrors.observe(viewLifecycleOwner) { errors ->
            tvErrors.text = coloredLines("  —  ") { i ->
                "${errors.getOrElse(i) { 0 }} / ${DuelViewModel.MAX_PLACE_ERRORS} erreurs"
            }
        }

        viewModel.placeTurn.observe(viewLifecycleOwner) { turn ->
            tvPlayer.text = "Au tour de ${playerName(turn)}"
            tvPlayer.setTextColor(playerColor(turn))
        }

        viewModel.placeFinished.observe(viewLifecycleOwner) { finished ->
            if (!finished) return@observe
            refreshCards()
            layoutInput.isVisible = false
            layoutResult.isVisible = true
            tvScore.text = coloredLines("\n") { i ->
                val n = players[i].score.toInt()
                "$n lieu${if (n > 1) "x" else ""} trouvé${if (n > 1) "s" else ""}"
            }
        }

        btnValidate.setOnClickListener {
            val input = etInput.text.toString()
            if (input.isBlank()) return@setOnClickListener
            val player = playerName(viewModel.placeTurn.value ?: 0)
            val message = when (val result = viewModel.submitPlaceAnswer(input, places)) {
                is DuelViewModel.PlaceTry.Found -> "✓ ${result.place.name} ($player)"
                DuelViewModel.PlaceTry.AlreadyFound -> "Déjà trouvé"
                DuelViewModel.PlaceTry.Wrong -> "Pas trouvé… ($player)"
                DuelViewModel.PlaceTry.Ignored -> null
            }
            etInput.text.clear()
            if (message != null) Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        btnRanking.setOnClickListener {
            findNavController().navigate(R.id.action_duelPlace_to_duelResult)
        }
    }
}
