package com.example.app_mythology.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R
import com.example.app_mythology.ui.common.PrimaryButton
import com.example.app_mythology.ui.common.bindPrimaryButtons

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.bindPrimaryButtons(
            listOf(
                PrimaryButton("data", "Données") {
                    findNavController().navigate(R.id.action_home_to_browseChoice)
                },
                PrimaryButton("quizz", "Quizz") {
                    findNavController().navigate(R.id.action_home_to_quizChoice)
                },
                PrimaryButton("duel", "Duel") {
                    findNavController().navigate(R.id.action_home_to_duel)
                },
            )
        )

        view.findViewById<View>(R.id.btn_trophy).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_achievements)
        }

        // Version affichée en haut à gauche, toujours issue de versionName (jamais codée en dur).
        val versionName = requireContext().packageManager
            .getPackageInfo(requireContext().packageName, 0).versionName
        view.findViewById<TextView>(R.id.tv_version).text = "V$versionName"
    }
}
