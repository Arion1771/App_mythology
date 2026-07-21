package com.example.app_mythology.ui.add

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R
import com.example.app_mythology.database.EntiteEntity
import com.example.app_mythology.viewmodel.EntiteViewModel

class AddEntityFragment : Fragment() {

    private val viewModel: EntiteViewModel by viewModels()

    // Races disponibles
    private val races = listOf(
        "God", "Titan", "Giant", "Heroes", "Monster",
        "Cyclope", "Hecatoncheires", "Muses", "Archangels",
        "Arthurian_Knight", "Demon_Prince", "Zodiacal_Sign"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_add_entity, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<EditText>(R.id.et_name)
        val spinnerMytho = view.findViewById<Spinner>(R.id.spinner_mythology)
        val btnAddMytho = view.findViewById<Button>(R.id.btn_add_mythology)
        val spinnerRace = view.findViewById<Spinner>(R.id.spinner_race)
        val btnSave = view.findViewById<Button>(R.id.btn_save)

        // Groupes de champs spécifiques par race
        val groupGod = view.findViewById<View>(R.id.group_god)
        val groupTitan = view.findViewById<View>(R.id.group_titan)
        val groupGiant = view.findViewById<View>(R.id.group_giant)
        val groupHeroes = view.findViewById<View>(R.id.group_heroes)
        val groupMonster = view.findViewById<View>(R.id.group_monster)
        val groupCyclope = view.findViewById<View>(R.id.group_cyclope)
        val groupMuses = view.findViewById<View>(R.id.group_muses)
        val groupArchangels = view.findViewById<View>(R.id.group_archangels)
        val groupKnight = view.findViewById<View>(R.id.group_knight)

        val allGroups = listOf(
            groupGod, groupTitan, groupGiant, groupHeroes, groupMonster,
            groupCyclope, groupMuses, groupArchangels, groupKnight
        )

        // Spinner mythologies (depuis la base + saisie libre)
        val mythologyList = mutableListOf<String>()
        viewModel.mythologies.observe(viewLifecycleOwner) { list ->
            mythologyList.clear()
            mythologyList.addAll(list)
            spinnerMytho.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                mythologyList
            ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        }

        // Ajout d'une nouvelle mythologie
        btnAddMytho.setOnClickListener {
            val et = EditText(requireContext())
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Nouvelle mythologie")
                .setView(et)
                .setPositiveButton("Ajouter") { _, _ ->
                    val newMytho = et.text.toString().trim()
                    if (newMytho.isNotEmpty() && !mythologyList.contains(newMytho)) {
                        mythologyList.add(newMytho)
                        (spinnerMytho.adapter as ArrayAdapter<*>).notifyDataSetChanged()
                        spinnerMytho.setSelection(mythologyList.indexOf(newMytho))
                    }
                }
                .setNegativeButton("Annuler", null)
                .show()
        }

        // Spinner races
        spinnerRace.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            races
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Afficher les champs selon la race sélectionnée
        spinnerRace.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                allGroups.forEach { it.isVisible = false }
                when (races[pos]) {
                    "God" -> groupGod.isVisible = true
                    "Titan" -> groupTitan.isVisible = true
                    "Giant" -> groupGiant.isVisible = true
                    "Heroes" -> groupHeroes.isVisible = true
                    "Monster" -> groupMonster.isVisible = true
                    "Cyclope" -> groupCyclope.isVisible = true
                    "Muses" -> groupMuses.isVisible = true
                    "Archangels" -> groupArchangels.isVisible = true
                    "Arthurian_Knight" -> groupKnight.isVisible = true
                }
            }
            override fun onNothingSelected(p: AdapterView<*>) {}
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val mythology = spinnerMytho.selectedItem?.toString() ?: ""
            val race = spinnerRace.selectedItem?.toString() ?: ""

            if (name.isEmpty() || mythology.isEmpty()) {
                Toast.makeText(requireContext(), "Nom et mythologie requis", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val entity = buildEntity(view, name, mythology, race)
            viewModel.insert(entity)
            findNavController().navigateUp()
        }
    }

    private fun buildEntity(view: View, name: String, mythology: String, race: String): EntiteEntity {
        return EntiteEntity(
            name = name,
            mythology = mythology,
            race = race,
            domain = view.findText(R.id.et_domain),
            godType = view.findText(R.id.et_godtype),
            equivalentName = view.findText(R.id.et_equivalent),
            fatherName = view.findText(R.id.et_father),
            motherName = view.findText(R.id.et_mother),
            giantType = view.findText(R.id.et_gianttype),
            opponentName = view.findText(R.id.et_opponent),
            story = view.findText(R.id.et_story),
            killer = view.findText(R.id.et_killer),
            ascendantName = view.findText(R.id.et_ascendant),
            monsterType = view.findText(R.id.et_monstertype),
            description = view.findText(R.id.et_description),
            primordial = view.findCheckbox(R.id.cb_primordial),
            museType = view.findText(R.id.et_musetype),
            role = view.findText(R.id.et_role),
            death = view.findText(R.id.et_death)
        )
    }

    private fun View.findText(id: Int): String? =
        try { (findViewById<EditText>(id)?.text?.toString()?.trim()).takeIf { !it.isNullOrEmpty() } }
        catch (e: Exception) { null }

    private fun View.findCheckbox(id: Int): Boolean? =
        try { findViewById<CheckBox>(id)?.isChecked } catch (e: Exception) { null }
}
