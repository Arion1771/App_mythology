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
import com.example.app_mythology.database.PlaceEntity
import com.example.app_mythology.viewmodel.PlaceViewModel

class PlaceDetailFragment : Fragment() {

    private val viewModel: PlaceViewModel by viewModels()
    private var placeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        placeId = arguments?.getInt("placeId") ?: -1
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_place_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.allPlaces.observe(viewLifecycleOwner) { list ->
            list.firstOrNull { it.id == placeId }?.let { bind(view, it) }
        }
    }

    private fun translateType(t: String) = when (t) {
        "Fleuve"  -> "Fleuve des Enfers"
        "Royaume" -> "Royaume"
        "Enfers"  -> "Lieu des Enfers"
        else      -> t
    }

    private fun bind(view: View, p: PlaceEntity) {
        view.findViewById<TextView>(R.id.tv_detail_name).text = p.name
        view.findViewById<TextView>(R.id.tv_detail_mythology).text = "Mythologie : ${p.mythology}"
        view.findViewById<TextView>(R.id.tv_detail_race).text = "Type : ${translateType(p.placeType)}"
        view.findViewById<TextView>(R.id.tv_detail_description).text = p.description

        bindOptional(view, R.id.tv_detail_particularity, R.id.row_particularity,
            p.particularity?.let { "Particularité : $it" })
        bindOptional(view, R.id.tv_detail_inhabitants, R.id.row_inhabitants,
            p.inhabitants?.let { "Habitants : $it" })
        bindOptional(view, R.id.tv_detail_region, R.id.row_region,
            p.souls?.let { "Âmes qui y séjournent : $it" })
    }

    private fun bindOptional(view: View, tvId: Int, rowId: Int, value: String?) {
        val row = view.findViewById<View>(rowId)
        if (value.isNullOrBlank()) { row.isVisible = false }
        else { row.isVisible = true; view.findViewById<TextView>(tvId).text = value }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_entity_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                findNavController().navigate(
                    R.id.action_placeDetail_to_editPlace,
                    bundleOf("placeId" to placeId)
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
