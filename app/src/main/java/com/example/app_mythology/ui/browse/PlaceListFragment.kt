package com.example.app_mythology.ui.browse

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_mythology.R
import com.example.app_mythology.database.PlaceEntity
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
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_places)
        val spinner  = view.findViewById<Spinner>(R.id.spinner_place_filter)
        val searchView = view.findViewById<SearchView>(R.id.search_view)

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

        // Une seule source observée à la fois : la recherche (si saisie) prime
        // sur le filtre du spinner, qui reprend la main quand le champ est vidé.
        var activeSource: LiveData<List<PlaceEntity>>? = null
        fun observe(source: LiveData<List<PlaceEntity>>) {
            activeSource?.removeObservers(viewLifecycleOwner)
            activeSource = source
            source.observe(viewLifecycleOwner) { adapter.submitList(it) }
        }
        fun spinnerSource() = when (spinner.selectedItemPosition) {
            1    -> viewModel.allRealms
            2    -> viewModel.allRivers
            3    -> viewModel.underworldPlaces
            else -> viewModel.allPlaces
        }

        searchView.queryHint = "Rechercher un lieu…"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = false
            override fun onQueryTextChange(q: String?): Boolean {
                observe(if (q.isNullOrBlank()) spinnerSource() else viewModel.search(q))
                return true
            }
        })

        observe(viewModel.allPlaces)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                if (searchView.query.isNullOrBlank()) observe(spinnerSource())
            }
            override fun onNothingSelected(p: AdapterView<*>) {}
        }
    }
}
