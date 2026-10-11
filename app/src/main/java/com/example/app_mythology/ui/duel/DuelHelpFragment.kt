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

            Après avoir choisi le nombre de joueurs et leurs noms, vous choisissez le type de question (Classique ou QCM), puis une difficulté commune (Facile, Moyen ou Difficile) : chaque joueur affronte 15 questions puisées parmi les entités de ce niveau.

            Vous choisissez ensuite si tous les joueurs répondent aux mêmes questions dans le même ordre, ou si chacun reçoit sa propre sélection.

            Le jeu se déroule ensuite en tours : chaque joueur répond à une question à son tour, sur 3 écrans — l'annonce du joueur et de son score, la question, puis un récapitulatif — jusqu'à ce que tout le monde ait répondu à toutes ses questions.

            En mode Classique, chaque question laisse deux essais (indice, puis toutes les informations en cas d'erreur) : une bonne réponse rapporte 2 points au premier essai, 1 point au second, 0 sinon. En mode QCM, un seul essai est possible parmi 4 noms proposés : 2 points si la réponse est juste, 0 sinon. Une question vaut le même nombre de points quelle que soit sa difficulté : chaque joueur joue donc sur 30 points.

            À deux joueurs uniquement, un troisième type de partie est proposé : Lieux. Après avoir choisi l'un des trois quiz Lieux (Arbre Monde, Fleuves de l'Enfer, Royaume des Morts), toute la partie se joue sur un seul écran, sur la même grille de cartes que le quiz Lieux solo. Le nom du joueur qui doit répondre s'affiche au-dessus du champ de saisie ; chacun a droit à un essai, puis la main passe à l'autre. Chaque joueur peut faire 3 erreurs : une fois ses 3 erreurs atteintes, il est éliminé et l'autre joueur continue seul. La partie se termine quand tous les lieux sont trouvés ou que les deux joueurs ont fait 3 erreurs ; chaque lieu trouvé rapporte alors 1 point à celui qui l'a trouvé.

            Une fois la partie terminée, un classement final affiche tous les joueurs du meilleur score au moins bon.
        """.trimIndent()
    }
}
