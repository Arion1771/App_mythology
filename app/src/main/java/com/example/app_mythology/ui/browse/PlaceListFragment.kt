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
import com.example.app_mythology.viewmodel.PlaceViewModel

class PlaceListFragment : Fragment() {

    private val viewModel: PlaceViewModel by viewModels()
    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_place_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_places)
        val spinner  = view.findViewById<Spinner>(R.id.spinner_place_filter)

        adapter = PlaceAdapter { place ->
            findNavController().navigate(
                R.id.action_placeList_to_placeDetail,
                bundleOf("placeId" to place.id)
            )
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        val options = listOf("Tous", "Royaumes", "Fleuves des Enfers", "Lieux des Enfers")
        spinner.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, options)
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        var activeSource = viewModel.allPlaces
        activeSource.observe(viewLifecycleOwner) { adapter.submitList(it) }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                activeSource.removeObservers(viewLifecycleOwner)
                activeSource = when (pos) {
                    1    -> viewModel.allRealms
                    2    -> viewModel.allRivers
                    3    -> viewModel.underworldPlaces
                    else -> viewModel.allPlaces
                }
                activeSource.observe(viewLifecycleOwner) { adapter.submitList(it) }
            }
            override fun onNothingSelected(p: AdapterView<*>) {}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.queryHint = "Rechercher un lieu…"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = false
            override fun onQueryTextChange(q: String?): Boolean {
                activeSource(q)
                return true
            }
        })
    }

    private fun activeSource(query: String?) {
        if (query.isNullOrBlank())
            viewModel.allPlaces.observe(viewLifecycleOwner) { adapter.submitList(it) }
        else
            viewModel.search(query).observe(viewLifecycleOwner) { adapter.submitList(it) }
    }
}
