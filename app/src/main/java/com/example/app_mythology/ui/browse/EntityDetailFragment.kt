package com.example.app_mythology.ui.browse

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.viewmodel.EntiteViewModel

class EntityDetailFragment : Fragment() {

    private val viewModel: EntiteViewModel by viewModels()
    private var entityId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entityId = arguments?.getInt("entityId") ?: -1
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_entity_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.entites.observe(viewLifecycleOwner) { list ->
            list.firstOrNull { it.id == entityId }?.let { bindEntity(view, it, list) }
        }
    }

    private fun bindEntity(view: View, e: EntiteEntity, allEntities: List<EntiteEntity>) {
        view.findViewById<TextView>(R.id.tv_detail_name).text = e.name
        view.findViewById<TextView>(R.id.tv_detail_mythology).text = "Mythologie : ${e.mythology}"
        view.findViewById<TextView>(R.id.tv_detail_race).text = "Race : ${translateRace(e.race)}"

        bind(view, R.id.tv_detail_domain,      R.id.row_domain,      e.domain)
        bind(view, R.id.tv_detail_godtype,     R.id.row_godtype,     e.godType?.let { translateGodType(it) })
        bind(view, R.id.tv_detail_gianttype,   R.id.row_gianttype,   e.giantType)
        bind(view, R.id.tv_detail_opponent,    R.id.row_opponent,    e.opponentName)
        bind(view, R.id.tv_detail_story,       R.id.row_story,       e.story)
        bind(view, R.id.tv_detail_killer,      R.id.row_killer,      e.killer)
        bind(view, R.id.tv_detail_ascendant,   R.id.row_ascendant,   e.ascendantName)
        bind(view, R.id.tv_detail_monstertype, R.id.row_monstertype, e.monsterType)
        bind(view, R.id.tv_detail_description, R.id.row_description, e.description)
        bind(view, R.id.tv_detail_musetype,    R.id.row_musetype,    e.museType)
        bind(view, R.id.tv_detail_role,        R.id.row_role,        e.role)
        bind(view, R.id.tv_detail_death,       R.id.row_death,       e.death)
        bind(view, R.id.tv_detail_father,      R.id.row_father,      e.fatherName)
        bind(view, R.id.tv_detail_mother,      R.id.row_mother,      e.motherName)

        // Primordial
        e.primordial?.let {
            view.findViewById<TextView>(R.id.tv_detail_primordial).text = if (it) "Oui" else "Non"
            view.findViewById<View>(R.id.row_primordial).isVisible = true
        } ?: run { view.findViewById<View>(R.id.row_primordial).isVisible = false }

        // Équivalent : cliquable si l'entité existe dans la base
        val equivRow = view.findViewById<View>(R.id.row_equivalent)
        val equivTv  = view.findViewById<TextView>(R.id.tv_detail_equivalent)
        if (!e.equivalentName.isNullOrBlank()) {
            equivRow.isVisible = true
            val linkedEntity = allEntities.firstOrNull { it.name == e.equivalentName }
            equivTv.text = e.equivalentName
            if (linkedEntity != null) {
                // Souligné et cliquable → ouvre la fiche de l'équivalent
                equivTv.paintFlags = equivTv.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
                equivTv.setTextColor(android.graphics.Color.parseColor("#FFB36A"))
                equivTv.setOnClickListener {
                    findNavController().navigate(
                        R.id.action_entityDetail_to_entityDetail,
                        bundleOf("entityId" to linkedEntity.id)
                    )
                }
            } else {
                equivTv.paintFlags = equivTv.paintFlags and android.graphics.Paint.UNDERLINE_TEXT_FLAG.inv()
                equivTv.setTextColor(android.graphics.Color.parseColor("#FFEAEAEA"))
                equivTv.setOnClickListener(null)
            }
        } else {
            equivRow.isVisible = false
        }
    }

    private fun bind(view: View, tvId: Int, rowId: Int, value: String?) {
        val row = view.findViewById<View>(rowId)
        if (value.isNullOrBlank()) {
            row.isVisible = false
        } else {
            row.isVisible = true
            view.findViewById<TextView>(tvId).text = value
        }
    }

    private fun translateRace(race: String) = when (race) {
        "God"              -> "Dieu"
        "Titan"            -> "Titan"
        "Giant"            -> "Géant"
        "Heroes"           -> "Héros"
        "Monster"          -> "Monstre"
        "Cyclope"          -> "Cyclope"
        "Hecatoncheires"   -> "Hécatonchire"
        "Muses"            -> "Muse"
        "Erinyes"          -> "Érinye"
        "Grées"            -> "Grée"
        "Valkyrie"         -> "Valkyrie"
        "Archangels"       -> "Archange"
        "Arthurian_Knight" -> "Chevalier Arthurien"
        "Demon_Prince"     -> "Démon"
        "Zodiacal_Sign"    -> "Signe du Zodiaque"
        else               -> race
    }

    private fun translateGodType(t: String) = when (t) {
        "Olympien" -> "Olympien"; "Primordial" -> "Primordial"
        "Ase" -> "Ase"; "Vane" -> "Vane"; "Norne" -> "Norne"
        "Parque" -> "Parque"; "Deva" -> "Deva"; "Yaksha" -> "Yaksha"
        "Asura" -> "Asura"; "Naga" -> "Naga"; "Rakshasa" -> "Rakshasa"
        "Humain" -> "Humain"; "Autre" -> "Autre"
        else -> t
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_entity_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                findNavController().navigate(
                    R.id.action_entityDetail_to_editEntity,
                    bundleOf("entityId" to entityId)
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
