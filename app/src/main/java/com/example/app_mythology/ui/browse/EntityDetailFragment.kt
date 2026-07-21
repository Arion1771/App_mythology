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
            val entity = list.firstOrNull { it.id == entityId } ?: return@observe
            bindEntity(view, entity)
        }
    }

    private fun bindEntity(view: View, e: EntiteEntity) {
        view.findViewById<TextView>(R.id.tv_detail_name).text = e.name
        view.findViewById<TextView>(R.id.tv_detail_mythology).text = e.mythology
        view.findViewById<TextView>(R.id.tv_detail_race).text = e.race

        bindOptional(view, R.id.tv_detail_domain, R.id.row_domain, e.domain)
        bindOptional(view, R.id.tv_detail_godtype, R.id.row_godtype, e.godType)
        bindOptional(view, R.id.tv_detail_equivalent, R.id.row_equivalent, e.equivalentName)
        bindOptional(view, R.id.tv_detail_father, R.id.row_father, e.fatherName)
        bindOptional(view, R.id.tv_detail_mother, R.id.row_mother, e.motherName)
        bindOptional(view, R.id.tv_detail_gianttype, R.id.row_gianttype, e.giantType)
        bindOptional(view, R.id.tv_detail_opponent, R.id.row_opponent, e.opponentName)
        bindOptional(view, R.id.tv_detail_story, R.id.row_story, e.story)
        bindOptional(view, R.id.tv_detail_killer, R.id.row_killer, e.killer)
        bindOptional(view, R.id.tv_detail_ascendant, R.id.row_ascendant, e.ascendantName)
        bindOptional(view, R.id.tv_detail_monstertype, R.id.row_monstertype, e.monsterType)
        bindOptional(view, R.id.tv_detail_description, R.id.row_description, e.description)
        bindOptional(view, R.id.tv_detail_musetype, R.id.row_musetype, e.museType)
        bindOptional(view, R.id.tv_detail_role, R.id.row_role, e.role)
        bindOptional(view, R.id.tv_detail_death, R.id.row_death, e.death)

        e.primordial?.let {
            view.findViewById<TextView>(R.id.tv_detail_primordial).text =
                if (it) "Oui" else "Non"
            view.findViewById<View>(R.id.row_primordial).isVisible = true
        } ?: run { view.findViewById<View>(R.id.row_primordial).isVisible = false }
    }

    private fun bindOptional(view: View, tvId: Int, rowId: Int, value: String?) {
        val row = view.findViewById<View>(rowId)
        if (value.isNullOrBlank()) {
            row.isVisible = false
        } else {
            row.isVisible = true
            view.findViewById<TextView>(tvId).text = value
        }
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
