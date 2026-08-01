package com.example.app_mythology.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R
import com.example.app_mythology.quiz.ListThemeCatalog

class QuizListChoiceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_list_choice, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = view.findViewById<LinearLayout>(R.id.container_themes)
        ListThemeCatalog.all.forEach { theme ->
            val btn = Button(requireContext()).apply {
                text = theme.title
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
                setTextColor(ContextCompat.getColor(requireContext(), R.color.on_primary))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(52)
                ).also { it.bottomMargin = dpToPx(12) }
                setOnClickListener {
                    findNavController().navigate(
                        R.id.action_quizListChoice_to_quizList,
                        bundleOf("themeId" to theme.id)
                    )
                }
            }
            container.addView(btn)
        }
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()
}
