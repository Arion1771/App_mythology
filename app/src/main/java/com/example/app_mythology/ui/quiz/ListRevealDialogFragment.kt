package com.example.app_mythology.ui.quiz

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.app_mythology.R

/**
 * Écran plein écran affiché au clic sur une carte non trouvée du mode Liste :
 * toutes les informations sauf le nom (équivalent au rang 2 du mode Classique).
 * Le retour (bouton ou geste) referme le dialogue et revient à la liste sans
 * modifier les cartes déjà trouvées.
 */
class ListRevealDialogFragment : DialogFragment() {

    override fun getTheme(): Int = R.style.FullScreenDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_list_reveal, null)
        view.findViewById<TextView>(R.id.tv_reveal_info).text = arguments?.getString("detailText")
        view.findViewById<Button>(R.id.btn_reveal_close).setOnClickListener { dismiss() }
        dialog.setContentView(view)
        return dialog
    }

    companion object {
        fun newInstance(detailText: String) = ListRevealDialogFragment().apply {
            arguments = bundleOf("detailText" to detailText)
        }
    }
}
