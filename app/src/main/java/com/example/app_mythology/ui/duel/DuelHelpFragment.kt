package com.example.app_mythology.ui.duel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.app_mythology.R

class DuelHelpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_duel_help, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.tv_duel_help_body).text = """
            Le mode Duel oppose de 2 à 12 joueurs sur le même appareil, à tour de rôle, sur des questions d'entités.

            Après avoir choisi le nombre de joueurs et leurs noms, vous choisissez le type de question (Classique ou QCM), puis une difficulté commune (Facile, Moyen ou Difficile) qui fixe le nombre de questions que chaque joueur affrontera (10, 20 ou 30).

            Vous choisissez ensuite si tous les joueurs répondent aux mêmes questions dans le même ordre, ou si chacun reçoit sa propre sélection.

            Le jeu se déroule ensuite en tours : chaque joueur répond à une question à son tour, sur 3 écrans — l'annonce du joueur et de son score, la question, puis un récapitulatif — jusqu'à ce que tout le monde ait répondu à toutes ses questions.

            En mode Classique, chaque question laisse deux essais (indice, puis toutes les informations en cas d'erreur). En mode QCM, un seul essai est possible parmi 4 noms proposés.

            Une fois la partie terminée, un classement final affiche tous les joueurs du meilleur score au moins bon.
        """.trimIndent()
    }
}
