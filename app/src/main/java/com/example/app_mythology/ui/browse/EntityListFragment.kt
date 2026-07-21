package com.example.app_mythology.ui.browse

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_mythology.R
import com.example.app_mythology.viewmodel.EntiteViewModel

class EntityListFragment : Fragment() {

    private val viewModel: EntiteViewModel by viewModels()
    private lateinit var adapter: EntityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_entity_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_entities)
        val spinner  = view.findViewById<Spinner>(R.id.spinner_filter)

        adapter = EntityAdapter { entity ->
            findNavController().navigate(
                R.id.action_entityList_to_entityDetail,
                bundleOf("entityId" to entity.id)
            )
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        // Remplir le spinner une seule fois quand les deux LiveData sont disponibles
        var raceList    = listOf<String>()
        var mythoList   = listOf<String>()

        fun buildSpinner() {
            val options = mutableListOf("Toutes (par race)") +
                    raceList.map  { "Race : ${translateRace(it)}" } +
                    mythoList.map { "Mythologie : $it" }
            spinner.adapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_item, options)
                .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        }

        viewModel.races.observe(viewLifecycleOwner) { races ->
            raceList = races
            buildSpinner()
        }
        viewModel.mythologies.observe(viewLifecycleOwner) { mythologies ->
            mythoList = mythologies
            buildSpinner()
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, v: View?, pos: Int, id: Long) {
                val selected = parent.getItemAtPosition(pos).toString()
                when {
                    selected.startsWith("Race : ") -> {
                        // Retrouver la valeur originale (anglais)
                        val fr = selected.removePrefix("Race : ")
                        val en = raceList.firstOrNull { translateRace(it) == fr } ?: fr
                        viewModel.filterByRace(en)
                    }
                    selected.startsWith("Mythologie : ") ->
                        viewModel.filterByMythology(selected.removePrefix("Mythologie : "))
                    else -> viewModel.showAll()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) = viewModel.showAll()
        }

        viewModel.entites.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_entity_list, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Rechercher…"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = false
            override fun onQueryTextChange(q: String?): Boolean {
                if (q.isNullOrBlank()) viewModel.showAll() else viewModel.search(q)
                return true
            }
        })
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
        "Archangels"       -> "Archange"
        "Arthurian_Knight" -> "Chevalier Arthurien"
        "Demon_Prince"     -> "Démon"
        "Zodiacal_Sign"    -> "Signe du Zodiaque"
        else               -> race
    }
}
