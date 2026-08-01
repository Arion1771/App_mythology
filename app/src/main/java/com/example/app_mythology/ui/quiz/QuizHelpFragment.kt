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
            "Trois quiz, trois façons de jouer",
            "MythoBase propose trois quiz indépendants, chacun avec ses propres règles :\n\n" +
            "• Entités — devinez des dieux, héros, monstres et autres figures mythologiques.\n" +
            "• Lieux — retrouvez des royaumes, fleuves infernaux et autres lieux emblématiques.\n" +
            "• Artéfacts — identifiez des armes et objets magiques.\n\n" +
            "Faites défiler cet écran pour découvrir comment fonctionne chacun d'eux."
        ),
        HelpPage(
            "Quiz Entités et Artéfacts",
            "Ces deux quiz partagent le même principe : un indice s'affiche, à vous de deviner le nom exact.\n\n" +
            "Si votre première réponse est fausse, vous disposez d'un second essai — les informations complémentaires (mythologie, description, histoire…) apparaissent alors pour vous aider.\n\n" +
            "Une fois la question résolue (bonne réponse, ou deux essais épuisés), un écran dédié s'affiche : le nom apparaît en vert si trouvé du premier coup, en jaune du second coup, ou en rouge si les deux essais ont échoué, suivi de toutes les informations disponibles. Un bouton permet ensuite de passer à la question suivante."
        ),
        HelpPage(
            "Quiz Lieux",
            "Le quiz Lieux fonctionne différemment : une grille de cartes grisées s'affiche, une par lieu à trouver.\n\n" +
            "Touchez une carte pour révéler un indice sans dévoiler son nom, puis saisissez votre réponse dans le champ prévu. Une carte correctement devinée se retourne et affiche définitivement le nom du lieu.\n\n" +
            "Aucune limite d'essais ni de temps : prenez le temps de retrouver tous les lieux de la grille."
        ),
        HelpPage(
            "Difficulté et score",
            "Avant de lancer un quiz d'entités ou d'artéfacts, choisissez un niveau :\n\n" +
            "• Facile — 10 questions de difficulté 1\n" +
            "• Moyen — 20 questions (difficultés 1 et 2)\n" +
            "• Difficile — 30 questions (difficultés 1, 2 et 3)\n\n" +
            "Pendant le quiz, une pastille colorée indique la difficulté de la question en cours : vert (facile), jaune (moyen), rouge (difficile) — à ne pas confondre avec les couleurs de l'écran de résultat, qui indiquent elles votre performance sur la question.\n\n" +
            "Une bonne réponse du premier coup rapporte autant de points que le niveau de difficulté de la question ; du second coup, seulement la moitié. Le score final s'affiche sur le total de points possible."
        ),
        HelpPage(
            "Conventions de réponse",
            "Quelques règles simplifient la saisie de vos réponses :\n\n" +
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
