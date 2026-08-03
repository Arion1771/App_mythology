package com.example.app_mythology.ui.add

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.viewmodel.EntiteViewModel

class EditEntityFragment : AddEntityFragment() {

    private val vm: EntiteViewModel by viewModels()
    private var entityId: Int = -1

    private val races = listOf(
        "God", "Titan", "Giant", "Heroes", "Monster",
        "Cyclope", "Hecatoncheires", "Muses", "Erinyes", "Grées", "Valkyrie",
        "Archangels", "Arthurian_Knight", "Demon_Prince", "Zodiacal_Sign"
    )
    private val racesDisplay = listOf(
        "Dieu", "Titan", "Géant", "Héros", "Monstre",
        "Cyclope", "Hécatonchire", "Muse", "Érinye", "Grée", "Valkyrie",
        "Archange", "Chevalier Arthurien", "Démon", "Signe du Zodiaque"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entityId = arguments?.getInt("entityId") ?: -1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (entityId <= 0) return

        vm.entites.observe(viewLifecycleOwner) { list ->
            val entity = list.firstOrNull { it.id == entityId } ?: return@observe
            prefill(view, entity)
        }

        view.findViewById<Button>(R.id.btn_save).setOnClickListener {
            val name = view.getText(R.id.et_name)
            val mythology = view.findViewById<Spinner>(R.id.spinner_mythology)
                .selectedItem?.toString() ?: ""
            val race = races[view.findViewById<Spinner>(R.id.spinner_race).selectedItemPosition]
            if (name.isNullOrBlank() || mythology.isBlank()) {
                Toast.makeText(requireContext(), "Nom et mythologie requis", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            vm.update(buildEntity(view, name, mythology, race, entityId))
            findNavController().navigateUp()
        }
    }

    private fun prefill(view: View, e: EntiteEntity) {
        view.findViewById<EditText>(R.id.et_name).setText(e.name)

        val racePos = races.indexOf(e.race).takeIf { it >= 0 } ?: 0
        view.findViewById<Spinner>(R.id.spinner_race).setSelection(racePos)
        view.findViewById<Spinner>(R.id.spinner_difficulty).setSelection(e.difficulty - 1)

        setText(view, R.id.et_domain,          e.domain)
        setText(view, R.id.et_domain_titan,    e.domain)
        setText(view, R.id.et_domain_muses,    e.domain)
        setText(view, R.id.et_domain_demon,    e.domain)
        setText(view, R.id.et_godtype,         e.godType)
        setText(view, R.id.et_equivalent,      e.equivalentName)
        setText(view, R.id.et_equivalent_titan,e.equivalentName)
        setText(view, R.id.et_father,          e.fatherName)
        setText(view, R.id.et_mother,          e.motherName)
        setText(view, R.id.et_gianttype,       e.giantType)
        setText(view, R.id.et_opponent,        e.opponentName)
        setText(view, R.id.et_story,           e.story)
        setText(view, R.id.et_story_cyclope,   e.story)
        setText(view, R.id.et_story_knight,    e.story)
        setText(view, R.id.et_killer,          e.killer)
        setText(view, R.id.et_ascendant,       e.ascendantName)
        setText(view, R.id.et_monstertype,     e.monsterType)
        setText(view, R.id.et_description,     e.description)
        setText(view, R.id.et_musetype,        e.museType)
        setText(view, R.id.et_role,            e.role)
        setText(view, R.id.et_death,           e.death)
        setText(view, R.id.et_zodiactype,      e.zodiacType)
        setText(view, R.id.et_chinese,         e.chineseEquivalent)
        setText(view, R.id.et_popularculture,  e.popularCulture)
        setText(view, R.id.et_tags,            e.tags)
        setText(view, R.id.et_listthemes,      e.listThemes)

        e.primordial?.let {
            view.findViewById<CheckBox>(R.id.cb_primordial)?.isChecked = it
        }
    }

    private fun setText(view: View, id: Int, value: String?) {
        if (value == null) return
        try { view.findViewById<EditText>(id)?.setText(value) } catch (_: Exception) {}
    }
}
