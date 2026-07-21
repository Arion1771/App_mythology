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
import com.example.app_mythology.viewmodel.ArtifactViewModel

class ArtifactListFragment : Fragment() {

    private val viewModel: ArtifactViewModel by viewModels()
    private lateinit var adapter: ArtifactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_entity_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_entities)
        val spinner  = view.findViewById<Spinner>(R.id.spinner_filter)

        adapter = ArtifactAdapter { artifact ->
            findNavController().navigate(
                R.id.action_artifactList_to_artifactDetail,
                bundleOf("artifactId" to artifact.id)
            )
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        var typeList  = listOf<String>()
        var mythoList = listOf<String>()

        fun buildSpinner() {
            val options = mutableListOf("Tous (par type)") +
                    typeList.map  { "Type : $it" } +
                    mythoList.map { "Mythologie : $it" }
            spinner.adapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_item, options)
                .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        }

        viewModel.types.observe(viewLifecycleOwner) { types ->
            typeList = types
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
                    selected.startsWith("Type : ") ->
                        viewModel.filterByType(selected.removePrefix("Type : "))
                    selected.startsWith("Mythologie : ") ->
                        viewModel.filterByMythology(selected.removePrefix("Mythologie : "))
                    else -> viewModel.showAll()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) = viewModel.showAll()
        }

        viewModel.artifacts.observe(viewLifecycleOwner) { list ->
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
}
