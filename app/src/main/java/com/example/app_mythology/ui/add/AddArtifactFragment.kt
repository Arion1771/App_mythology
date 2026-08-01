package com.example.app_mythology.ui.add

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R
import com.example.app_mythology.database.ArtifactEntity
import com.example.app_mythology.viewmodel.ArtifactViewModel

open class AddArtifactFragment : Fragment() {

    protected val viewModel: ArtifactViewModel by viewModels()
    protected open val editMode = false
    protected var existingId: Int = -1

    private val typesInternal = listOf("Arme", "Artefact", "Objet magique", "Véhicule", "Nourriture")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_add_artifact, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName        = view.findViewById<EditText>(R.id.et_artifact_name)
        val spinnerMytho   = view.findViewById<Spinner>(R.id.spinner_artifact_mythology)
        val btnAddMytho    = view.findViewById<Button>(R.id.btn_add_artifact_mythology)
        val spinnerType    = view.findViewById<Spinner>(R.id.spinner_artifact_type)
        val btnSave        = view.findViewById<Button>(R.id.btn_artifact_save)

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

        spinnerType.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, typesInternal)
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        if (editMode && existingId > 0) {
            viewModel.artifacts.observe(viewLifecycleOwner) { list ->
                list.firstOrNull { it.id == existingId }?.let { a -> prefill(view, a, mythologyList, spinnerMytho, spinnerType) }
            }
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val mythology = spinnerMytho.selectedItem?.toString() ?: ""

            if (name.isEmpty() || mythology.isBlank()) {
                Toast.makeText(requireContext(), "Nom et mythologie requis", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val artifact = ArtifactEntity(
                id           = if (editMode) existingId else 0,
                name         = name,
                mythology    = mythology,
                artifactType = typesInternal[spinnerType.selectedItemPosition],
                ownerName    = view.getText(R.id.et_owner),
                creatorName  = view.getText(R.id.et_creator),
                power        = view.getText(R.id.et_power),
                story        = view.getText(R.id.et_artifact_story),
                description  = view.getText(R.id.et_artifact_description),
                tags         = view.getText(R.id.et_artifact_tags)
            )
            if (editMode) viewModel.update(artifact) else viewModel.insert(artifact)
            findNavController().navigateUp()
        }
    }

    private fun prefill(
        view: View, a: ArtifactEntity, mythologyList: List<String>,
        spinnerMytho: Spinner, spinnerType: Spinner
    ) {
        view.findViewById<EditText>(R.id.et_artifact_name).setText(a.name)
        val mythoPos = mythologyList.indexOf(a.mythology)
        if (mythoPos >= 0) spinnerMytho.setSelection(mythoPos)
        val typePos = typesInternal.indexOf(a.artifactType)
        if (typePos >= 0) spinnerType.setSelection(typePos)

        view.findEt(R.id.et_owner)?.setText(a.ownerName)
        view.findEt(R.id.et_creator)?.setText(a.creatorName)
        view.findEt(R.id.et_power)?.setText(a.power)
        view.findEt(R.id.et_artifact_story)?.setText(a.story)
        view.findEt(R.id.et_artifact_description)?.setText(a.description)
        view.findEt(R.id.et_artifact_tags)?.setText(a.tags)
    }

    private fun View.getText(id: Int): String? =
        try { findEt(id)?.text?.toString()?.trim().takeIf { !it.isNullOrEmpty() } }
        catch (_: Exception) { null }

    private fun View.findEt(id: Int): EditText? =
        try { findViewById(id) } catch (_: Exception) { null }
}
