package com.example.app_mythology.ui.add

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.app_mythology.R
import com.example.app_mythology.database.PlaceEntity
import com.example.app_mythology.viewmodel.PlaceViewModel

open class AddPlaceFragment : Fragment() {

    protected val viewModel: PlaceViewModel by viewModels()
    protected open val editMode = false
    protected var existingId: Int = -1

    // Types internes (stockés en base) et affichage français
    private val placeTypesInternal = listOf("Fleuve", "Royaume", "Enfers")
    private val placeTypesDisplay  = listOf("Fleuve des Enfers", "Royaume", "Lieu des Enfers")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_add_place, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName          = view.findViewById<EditText>(R.id.et_place_name)
        val etDescription   = view.findViewById<EditText>(R.id.et_place_description)
        val spinnerMytho    = view.findViewById<Spinner>(R.id.spinner_place_mythology)
        val spinnerType     = view.findViewById<Spinner>(R.id.spinner_place_type)
        val groupRealm      = view.findViewById<View>(R.id.group_realm)
        val groupRiver      = view.findViewById<View>(R.id.group_river)
        val groupUnderworld = view.findViewById<View>(R.id.group_underworld)
        val btnSave         = view.findViewById<Button>(R.id.btn_place_save)

        spinnerType.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, placeTypesDisplay)
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                groupRealm.isVisible      = placeTypesInternal[pos] == "Royaume"
                groupRiver.isVisible      = placeTypesInternal[pos] == "Fleuve"
                groupUnderworld.isVisible = placeTypesInternal[pos] == "Enfers"
            }
            override fun onNothingSelected(p: AdapterView<*>) {}
        }

        val mythologies = mutableListOf("Grecque", "Nordique", "Égyptienne", "Autre")
        spinnerMytho.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, mythologies)
            .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        if (editMode && existingId > 0) {
            viewModel.allPlaces.observe(viewLifecycleOwner) { list ->
                list.firstOrNull { it.id == existingId }?.let { p ->
                    etName.setText(p.name)
                    etDescription.setText(p.description)
                    val typePos = placeTypesInternal.indexOf(p.placeType)
                    if (typePos >= 0) spinnerType.setSelection(typePos)
                    val mythoPos = mythologies.indexOf(p.mythology)
                    if (mythoPos >= 0) spinnerMytho.setSelection(mythoPos)
                    view.findEt(R.id.et_inhabitants)?.setText(p.inhabitants)
                    view.findEt(R.id.et_particularity)?.setText(p.particularity)
                    view.findEt(R.id.et_souls)?.setText(p.souls)
                }
            }
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val desc = etDescription.text.toString().trim()
            val mytho = spinnerMytho.selectedItem.toString()
            val typeInternal = placeTypesInternal[spinnerType.selectedItemPosition]

            if (name.isEmpty() || desc.isEmpty()) {
                Toast.makeText(requireContext(), "Nom et description requis", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val place = PlaceEntity(
                id            = if (editMode) existingId else 0,
                name          = name,
                mythology     = mytho,
                description   = desc,
                placeType     = typeInternal,
                inhabitants   = view.getText(R.id.et_inhabitants),
                particularity = view.getText(R.id.et_particularity),
                souls         = view.getText(R.id.et_souls)
            )
            if (editMode) viewModel.update(place) else viewModel.insert(place)
            findNavController().navigateUp()
        }
    }

    private fun View.getText(id: Int): String? =
        try { findEt(id)?.text?.toString()?.trim().takeIf { !it.isNullOrEmpty() } }
        catch (_: Exception) { null }

    private fun View.findEt(id: Int): EditText? =
        try { findViewById(id) } catch (_: Exception) { null }
}
