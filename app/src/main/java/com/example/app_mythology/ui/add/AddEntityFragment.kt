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

open class AddEntityFragment : Fragment() {

    protected val viewModel: EntiteViewModel by viewModels()

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
    private val difficulties = listOf("Facile", "Moyen", "Difficile")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_add_entity, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerMytho = view.findViewById<Spinner>(R.id.spinner_mythology)
        val btnAddMytho  = view.findViewById<Button>(R.id.btn_add_mythology)
        val spinnerRace  = view.findViewById<Spinner>(R.id.spinner_race)
        val btnSave      = view.findViewById<Button>(R.id.btn_save)

        val groups = mapOf(
            "God"              to R.id.group_god,
            "Titan"            to R.id.group_titan,
            "Giant"            to R.id.group_giant,
            "Heroes"           to R.id.group_heroes,
            "Monster"          to R.id.group_monster,
            "Cyclope"          to R.id.group_cyclope,
            "Hecatoncheires"   to null,
            "Muses"            to R.id.group_muses,
            "Erinyes"          to null,
            "Grées"            to null,
            "Valkyrie"         to null,
            "Archangels"       to R.id.group_archangels,
            "Arthurian_Knight" to R.id.group_knight,
            "Demon_Prince"     to R.id.group_demon,
            "Zodiacal_Sign"    to R.id.group_zodiac
        )

        // Mythologies depuis la base
        val mythologyList = mutableListOf<String>()
        viewModel.mythologies.observe(viewLifecycleOwner) { list ->
            mythologyList.clear()
            mythologyList.addAll(list)
            spinnerMytho.adapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_item, mythologyList)
                .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        }

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
                .setNegativeButton("Annuler", null).show()
        }

        // Spinner races (affiché en français)
        spinnerRace.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, racesDisplay)
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Spinner niveau (difficulté)
        view.findViewById<Spinner>(R.id.spinner_difficulty).adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, difficulties)
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Afficher le bon groupe selon la race
        spinnerRace.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                groups.values.forEach { resId ->
                    resId?.let { view.findViewById<View>(it).isVisible = false }
                }
                groups[races[pos]]?.let { view.findViewById<View>(it).isVisible = true }
            }
            override fun onNothingSelected(p: AdapterView<*>) {}
        }

        btnSave.setOnClickListener {
            val name = view.getText(R.id.et_name)
            val mythology = spinnerMytho.selectedItem?.toString() ?: ""
            val race = races[spinnerRace.selectedItemPosition]

            if (name.isNullOrBlank() || mythology.isBlank()) {
                Toast.makeText(requireContext(), "Nom et mythologie requis", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.insert(buildEntity(view, name, mythology, race))
            findNavController().navigateUp()
        }
    }

    protected open fun buildEntity(view: View, name: String, mythology: String, race: String,
                                    existingId: Int = 0): EntiteEntity {
        // Lire le champ domain selon la race active
        val domain = when (race) {
            "God"          -> view.getText(R.id.et_domain)
            "Titan"        -> view.getText(R.id.et_domain_titan)
            "Muses"        -> view.getText(R.id.et_domain_muses)
            "Demon_Prince" -> view.getText(R.id.et_domain_demon)
            else           -> null
        }
        val story = when (race) {
            "Heroes"           -> view.getText(R.id.et_story)
            "Cyclope"          -> view.getText(R.id.et_story_cyclope)
            "Arthurian_Knight" -> view.getText(R.id.et_story_knight)
            else               -> null
        }
        val equivalent = when (race) {
            "God"   -> view.getText(R.id.et_equivalent)
            "Titan" -> view.getText(R.id.et_equivalent_titan)
            else    -> null
        }
        val difficulty = view.findViewById<Spinner>(R.id.spinner_difficulty).selectedItemPosition + 1
        return EntiteEntity(
            id                = existingId,
            name              = name,
            mythology         = mythology,
            race              = race,
            difficulty        = difficulty,
            domain            = domain,
            godType           = view.getText(R.id.et_godtype),
            equivalentName    = equivalent,
            fatherName        = view.getText(R.id.et_father),
            motherName        = view.getText(R.id.et_mother),
            giantType         = view.getText(R.id.et_gianttype),
            opponentName      = view.getText(R.id.et_opponent),
            story             = story,
            killer            = view.getText(R.id.et_killer),
            ascendantName     = view.getText(R.id.et_ascendant),
            monsterType       = view.getText(R.id.et_monstertype),
            description       = view.getText(R.id.et_description),
            primordial        = view.findCheckbox(R.id.cb_primordial),
            museType          = view.getText(R.id.et_musetype),
            role              = view.getText(R.id.et_role),
            death             = view.getText(R.id.et_death),
            zodiacType        = view.getText(R.id.et_zodiactype),
            chineseEquivalent = view.getText(R.id.et_chinese),
            popularCulture    = view.getText(R.id.et_popularculture),
            tags              = view.getText(R.id.et_tags),
            listThemes        = view.getText(R.id.et_listthemes)
        )
    }

    protected fun View.getText(id: Int): String? =
        try { findViewById<EditText>(id)?.text?.toString()?.trim().takeIf { !it.isNullOrEmpty() } }
        catch (_: Exception) { null }

    private fun View.findCheckbox(id: Int): Boolean? =
        try { findViewById<CheckBox>(id)?.isChecked } catch (_: Exception) { null }
}
