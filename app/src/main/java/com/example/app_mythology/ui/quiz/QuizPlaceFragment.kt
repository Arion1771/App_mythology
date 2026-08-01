package com.example.app_mythology.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.app_mythology.R
import com.example.app_mythology.database.PlaceEntity
import com.example.app_mythology.viewmodel.QuizViewModel

/**
 * Fragment générique pour les 3 quiz de lieux.
 * Argument "quizType" : "yggdrasil" | "rivers" | "underworld"
 */
class QuizPlaceFragment : Fragment() {

    private val viewModel: QuizViewModel by viewModels()
    private lateinit var quizType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizType = arguments?.getString("quizType") ?: "yggdrasil"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_place, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tv_place_quiz_title)
        val container = view.findViewById<GridLayout>(R.id.grid_places)
        val etInput = view.findViewById<EditText>(R.id.et_place_answer)
        val btnValidate = view.findViewById<Button>(R.id.btn_place_validate)
        val tvFoundCount = view.findViewById<TextView>(R.id.tv_found_count)

        tvTitle.text = when (quizType) {
            "yggdrasil" -> "Arbre Monde"
            "rivers" -> "Fleuves de l'Enfer"
            else -> "Le Royaume des Morts"
        }

        viewModel.loadPlaceQuizzes()

        // Choisir la bonne liste selon le quiz
        val placesLiveData = when (quizType) {
            "yggdrasil" -> viewModel.yggdrasilRealms
            "rivers" -> viewModel.hellRivers
            else -> viewModel.underworldPlaces
        }

        var places = listOf<PlaceEntity>()

        fun updateFoundCount() {
            val found = viewModel.foundIds.value ?: emptySet()
            tvFoundCount.text = "${found.size} / ${places.size} trouvés"
        }

        placesLiveData.observe(viewLifecycleOwner) { list ->
            places = list
            container.removeAllViews()
            container.columnCount = 3

            list.forEach { place ->
                val card = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_place_card, container, false)
                val tvCardName = card.findViewById<TextView>(R.id.tv_card_name)
                val tvCardInfo = card.findViewById<TextView>(R.id.tv_card_info)

                // Initialement grisé
                tvCardName.isVisible = false
                tvCardInfo.isVisible = false
                card.tag = place.id

                // Clic sur une carte grisée → afficher l'info sans révéler le nom
                card.setOnClickListener {
                    val found = viewModel.foundIds.value ?: emptySet()
                    if (place.id !in found) {
                        val info = when (quizType) {
                            "yggdrasil" -> place.inhabitants ?: "—"
                            "rivers" -> place.particularity ?: "—"
                            else -> place.description
                        }
                        tvCardInfo.text = info
                        tvCardInfo.isVisible = true
                    }
                }
                container.addView(card)
            }

            // Le nombre total de lieux n'est connu qu'une fois cette liste chargée :
            // sans cet appel, le compteur reste bloqué sur "0 / 0" tant qu'aucun lieu
            // n'a encore été trouvé (foundIds ne se met à jour qu'à la première trouvaille).
            updateFoundCount()
        }

        // Observer les cartes trouvées pour les retourner
        viewModel.foundIds.observe(viewLifecycleOwner) { found ->
            for (i in 0 until container.childCount) {
                val card = container.getChildAt(i)
                val id = card.tag as? Int ?: continue
                if (id in found) {
                    val place = places.firstOrNull { it.id == id } ?: continue
                    card.findViewById<TextView>(R.id.tv_card_name).apply {
                        text = place.name
                        isVisible = true
                    }
                    card.findViewById<TextView>(R.id.tv_card_info).isVisible = false
                    card.setBackgroundResource(R.drawable.card_found_bg)
                }
            }
            updateFoundCount()
        }

        btnValidate.setOnClickListener {
            val input = etInput.text.toString()
            val found = viewModel.checkPlaceAnswer(input, places)
            if (found != null) {
                viewModel.markPlaceFound(found.id)
                etInput.text.clear()
                Toast.makeText(requireContext(), "✓ ${found.name}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Pas trouvé…", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
