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
import com.example.app_mythology.database.ArtifactEntity
import com.example.app_mythology.viewmodel.ArtifactViewModel

class ArtifactDetailFragment : Fragment() {

    private val viewModel: ArtifactViewModel by viewModels()
    private var artifactId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        artifactId = arguments?.getInt("artifactId") ?: -1
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_artifact_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.artifacts.observe(viewLifecycleOwner) { list ->
            list.firstOrNull { it.id == artifactId }?.let { bind(view, it) }
        }
    }

    private fun bind(view: View, a: ArtifactEntity) {
        view.findViewById<TextView>(R.id.tv_detail_name).text = a.name
        view.findViewById<TextView>(R.id.tv_detail_mythology).text = "Mythologie : ${a.mythology}"
        view.findViewById<TextView>(R.id.tv_detail_race).text = "Type : ${a.artifactType}"

        bindOptional(view, R.id.tv_detail_owner, R.id.row_owner, a.ownerName)
        bindOptional(view, R.id.tv_detail_creator, R.id.row_creator, a.creatorName)
        bindOptional(view, R.id.tv_detail_power, R.id.row_power, a.power)
        bindOptional(view, R.id.tv_detail_story, R.id.row_story, a.story)
        bindOptional(view, R.id.tv_detail_description, R.id.row_description, a.description)
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
                    R.id.action_artifactDetail_to_editArtifact,
                    bundleOf("artifactId" to artifactId)
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
