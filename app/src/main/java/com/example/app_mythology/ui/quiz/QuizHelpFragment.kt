package com.example.app_mythology.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.app_mythology.R

class QuizHelpFragment : Fragment() {

    private data class HelpPage(val title: String, val body: String)

    private val pages = listOf(
        HelpPage(
            "Trois types de quiz",
            "MythoBase propose trois types de quiz :\n\n" +
            "• Classique — un indice, à vous de deviner le nom (entités et artéfacts).\n" +
            "• QCM — la même question, mais avec 4 noms proposés à choisir (entités et artéfacts).\n" +
            "• Liste — retrouvez toutes les entrées d'un thème choisi, façon quiz Lieux (entités, artéfacts et lieux).\n\n" +
            "Faites défiler cet écran pour découvrir comment fonctionne chacun d'eux."
        ),
        HelpPage(
            "Mode Classique (Entités et Artéfacts)",
            "Un indice s'affiche, à vous de deviner le nom exact.\n\n" +
            "Si votre première réponse est fausse, vous disposez d'un second essai — les informations complémentaires (mythologie, description, histoire…) apparaissent alors pour vous aider.\n\n" +
            "Une fois la question résolue (bonne réponse, ou deux essais épuisés), un écran dédié s'affiche : le nom apparaît en vert si trouvé du premier coup, en jaune du second coup, ou en rouge si les deux essais ont échoué, suivi de toutes les informations disponibles. Un bouton permet ensuite de passer à la question suivante."
        ),
        HelpPage(
            "Mode QCM (Entités et Artéfacts)",
            "Même question que le mode Classique (même indice, mêmes informations affichées), mais la réponse se donne en un seul coup, en touchant l'un des 4 noms proposés.\n\n" +
            "Les 3 noms erronés (les leurres) sont choisis pour ressembler à la bonne réponse : mythologie proche, thématique commune, équivalent dans une autre mythologie…\n\n" +
            "Un seul essai est possible : que la réponse soit juste ou fausse, un écran dédié s'affiche aussitôt avec le nom en vert (correct) ou en rouge (faux) et toutes les informations disponibles, suivi d'un bouton pour passer à la question suivante — sans second essai, contrairement au mode Classique. Un écran de score final s'affiche à la fin."
        ),
        HelpPage(
            "Quiz Lieux",
            "Le quiz Lieux fonctionne différemment : une grille de cartes grisées s'affiche, une par lieu à trouver.\n\n" +
            "Touchez une carte pour révéler un indice sans dévoiler son nom, puis saisissez votre réponse dans le champ prévu. Une carte correctement devinée se retourne et affiche définitivement le nom du lieu.\n\n" +
            "Vous disposez de 5 essais au total : seule une réponse fausse en consomme un (4 erreurs sont tolérées, la 5ᵉ termine le quiz). Le nombre d'erreurs est affiché en permanence.\n\n" +
            "Le quiz se termine dès que tous les lieux sont trouvés, ou après la 5ᵉ erreur : toutes les cartes sont alors révélées, en vert si trouvées, en rouge sinon, suivies d'un écran de score indiquant le nombre de lieux trouvés sur le total."
        ),
        HelpPage(
            "Mode Liste",
            "Choisissez d'abord un thème (mythologie, famille de créatures, groupe légendaire…) : une grille de cartes grisées s'affiche, une par entrée à trouver — certains thèmes regroupent leurs cartes sous plusieurs sous-titres (ex. Muses classiques / béotiennes).\n\n" +
            "Touchez une carte non trouvée pour ouvrir un écran complet listant toutes ses informations sauf le nom (comme le second essai du mode Classique) ; un retour vous ramène à la grille sans rien perdre de votre progression. Saisissez ensuite votre réponse dans le champ prévu, comme pour le quiz Lieux.\n\n" +
            "Le nombre d'essais autorisés dépend de la taille du thème : 3 pour les thèmes de moins de 20 entrées, 5 pour ceux de moins de 50, 10 au-delà. Le quiz se termine dès que tout est trouvé ou une fois les essais épuisés : toutes les cartes sont alors révélées (vert si trouvée, rouge sinon), suivies d'un score sur le total de la liste."
        ),
        HelpPage(
            "Difficulté et score",
            "Avant de lancer un quiz d'entités ou d'artéfacts (Classique ou QCM), choisissez un niveau :\n\n" +
            "• Facile — 10 questions de difficulté 1\n" +
            "• Moyen — 20 questions (difficultés 1 et 2)\n" +
            "• Difficile — 30 questions (difficultés 1, 2 et 3)\n\n" +
            "Pendant le quiz, une pastille colorée indique la difficulté de la question en cours : vert (facile), jaune (moyen), rouge (difficile) — à ne pas confondre avec les couleurs de l'écran de résultat, qui indiquent elles votre performance sur la question.\n\n" +
            "En mode Classique, une bonne réponse du premier coup rapporte autant de points que le niveau de difficulté de la question ; du second coup, seulement la moitié. En mode QCM, une bonne réponse rapporte tous les points de la question (un seul essai possible), une mauvaise n'en rapporte aucun. Le score final s'affiche sur le total de points possible.\n\n" +
            "Le mode Liste et le quiz Lieux n'ont pas de niveau de difficulté ni de score par points : le résultat final indique simplement le nombre d'entrées trouvées sur le total."
        ),
        HelpPage(
            "Conventions de réponse",
            "Quelques règles simplifient la saisie de vos réponses (modes Classique, Lieux et Liste — le mode QCM se joue en touchant directement une proposition) :\n\n" +
            "• La casse n'a aucune importance (« zeus » et « ZEUS » sont acceptés).\n" +
            "• Les accents ne sont pas pris en compte (« Zéus » est accepté pour « Zeus »).\n" +
            "• Aucun nom de la base ne commence par un article (« le », « la », « les », « l' ») : inutile d'en ajouter un dans votre réponse.\n\n" +
            "Seul le nom exact (hors casse et accents) est accepté — les surnoms ou équivalents dans une autre mythologie ne comptent pas comme réponse valide."
        ),
    )

    private val dots = mutableListOf<View>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_quiz_help, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = view.findViewById<ViewPager2>(R.id.viewpager_help)
        val dotsContainer = view.findViewById<LinearLayout>(R.id.dots_help_container)

        viewPager.adapter = HelpPagerAdapter(pages)

        dots.clear()
        dotsContainer.removeAllViews()
        pages.forEachIndexed { index, _ ->
            val dot = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(20, 20).also { p ->
                    p.marginStart = 6; p.marginEnd = 6
                }
                setBackgroundResource(if (index == 0) R.drawable.dot_page_selected else R.drawable.dot_neutral)
            }
            dotsContainer.addView(dot)
            dots.add(dot)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                dots.forEachIndexed { index, dot ->
                    dot.setBackgroundResource(
                        if (index == position) R.drawable.dot_page_selected else R.drawable.dot_neutral
                    )
                }
            }
        })
    }

    private class HelpPagerAdapter(private val pages: List<HelpPage>) :
        RecyclerView.Adapter<HelpPagerAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.tv_help_title)
            val body: TextView = view.findViewById(R.id.tv_help_body)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_help_page, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.title.text = pages[position].title
            holder.body.text = pages[position].body
        }

        override fun getItemCount() = pages.size
    }
}
