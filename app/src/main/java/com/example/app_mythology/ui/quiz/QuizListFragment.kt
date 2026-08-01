package com.example.app_mythology.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R
import com.example.app_mythology.quiz.ListItem
import com.example.app_mythology.quiz.ListThemeCatalog
import com.example.app_mythology.viewmodel.QuizViewModel

/** Quiz Liste : retrouver toutes les entrées d'un thème choisi. Argument "themeId". */
class QuizListFragment : Fragment() {

    private val viewModel: QuizViewModel by viewModels()
    private lateinit var themeId: String
    private val cardViews = mutableMapOf<Int, View>()
    private var items = listOf<ListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeId = arguments?.getString("themeId") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val theme = ListThemeCatalog.byId(themeId)
        val tvTitle       = view.findViewById<TextView>(R.id.tv_list_quiz_title)
        val groupsContainer = view.findViewById<LinearLayout>(R.id.container_list_groups)
        val etInput       = view.findViewById<EditText>(R.id.et_list_answer)
        val btnValidate   = view.findViewById<Button>(R.id.btn_list_validate)
        val tvFoundCount  = view.findViewById<TextView>(R.id.tv_list_found_count)
        val tvAttempts    = view.findViewById<TextView>(R.id.tv_list_attempts)
        val layoutInput   = view.findViewById<View>(R.id.layout_list_input)
        val layoutResult  = view.findViewById<View>(R.id.layout_list_result)
        val tvScore       = view.findViewById<TextView>(R.id.tv_list_score)
        val btnRestart    = view.findViewById<Button>(R.id.btn_list_restart)

        tvTitle.text = theme?.title ?: "Liste"

        viewModel.loadListQuiz(themeId)

        fun updateFoundCount() {
            val found = viewModel.listFoundIds.value ?: emptySet()
            tvFoundCount.text = "${found.size} / ${items.size} trouvés"
        }

        fun updateAttempts() {
            val wrong = viewModel.listWrongAttempts.value ?: 0
            tvAttempts.text = "Erreurs : $wrong / ${viewModel.currentListMaxErrors()}"
        }

        fun cardBg(found: Boolean, revealedFinal: Boolean, isCorrectFinal: Boolean) = when {
            revealedFinal && isCorrectFinal  -> R.drawable.card_found_final_bg
            revealedFinal && !isCorrectFinal -> R.drawable.card_notfound_bg
            found                             -> R.drawable.card_found_bg
            else                              -> R.drawable.card_hidden_bg
        }

        fun revealAllCards(found: Set<Int>) {
            cardViews.forEach { (id, card) ->
                val item = items.firstOrNull { it.id == id } ?: return@forEach
                card.findViewById<TextView>(R.id.tv_card_info).isVisible = false
                card.findViewById<TextView>(R.id.tv_card_name).apply {
                    text = item.name
                    isVisible = true
                }
                card.setBackgroundResource(cardBg(found = id in found, revealedFinal = true, isCorrectFinal = id in found))
            }
        }

        viewModel.listGroups.observe(viewLifecycleOwner) { groups ->
            items = groups.flatMap { it.items }
            groupsContainer.removeAllViews()
            cardViews.clear()

            groups.forEach { group ->
                if (group.items.isEmpty()) return@forEach
                if (group.header != null) {
                    val headerTv = TextView(requireContext()).apply {
                        text = group.header
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.on_surface))
                        textSize = 15f
                        setTypeface(typeface, android.graphics.Typeface.BOLD)
                        setPadding(0, dp(12), 0, dp(6))
                    }
                    groupsContainer.addView(headerTv)
                }
                val grid = GridLayout(requireContext()).apply {
                    columnCount = 3
                    useDefaultMargins = true
                }
                group.items.forEach { item ->
                    val card = LayoutInflater.from(requireContext())
                        .inflate(R.layout.item_place_card, grid, false)
                    card.tag = item.id
                    card.findViewById<TextView>(R.id.tv_card_name).isVisible = false
                    card.setOnClickListener {
                        val found = viewModel.listFoundIds.value ?: emptySet()
                        if (item.id !in found) {
                            ListRevealDialogFragment.newInstance(item.detailText)
                                .show(childFragmentManager, "reveal")
                        }
                    }
                    grid.addView(card)
                    cardViews[item.id] = card
                }
                groupsContainer.addView(grid)
            }

            updateFoundCount()
            if (viewModel.listFinished.value == true) {
                revealAllCards(viewModel.listFoundIds.value ?: emptySet())
            }
        }

        viewModel.listFoundIds.observe(viewLifecycleOwner) { found ->
            cardViews.forEach { (id, card) ->
                if (id in found) {
                    val item = items.firstOrNull { it.id == id } ?: return@forEach
                    card.findViewById<TextView>(R.id.tv_card_name).apply {
                        text = item.name
                        isVisible = true
                    }
                    card.setBackgroundResource(R.drawable.card_found_bg)
                }
            }
            updateFoundCount()
        }

        viewModel.listWrongAttempts.observe(viewLifecycleOwner) { updateAttempts() }

        viewModel.listFinished.observe(viewLifecycleOwner) { finished ->
            if (finished) {
                val found = viewModel.listFoundIds.value ?: emptySet()
                revealAllCards(found)
                layoutInput.isVisible = false
                layoutResult.isVisible = true
                tvScore.text = "Score : ${found.size} / ${items.size} trouvés"
            }
        }

        btnValidate.setOnClickListener {
            val input = etInput.text.toString()
            val found = viewModel.checkListAnswer(input)
            if (found != null) {
                viewModel.markListFound(found.id)
                etInput.text.clear()
                Toast.makeText(requireContext(), "✓ ${found.name}", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.registerWrongListAttempt()
                val remaining = viewModel.currentListMaxErrors() - (viewModel.listWrongAttempts.value ?: 0)
                val message = when {
                    remaining <= 0 -> "Pas trouvé…"
                    remaining == 1 -> "Pas trouvé… (1 essai restant)"
                    else -> "Pas trouvé… ($remaining essais restants)"
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        btnRestart.setOnClickListener { findNavController().navigateUp() }
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
