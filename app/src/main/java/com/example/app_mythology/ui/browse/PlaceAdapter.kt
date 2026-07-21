package com.example.app_mythology.ui.browse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app_mythology.R
import com.example.app_mythology.database.PlaceEntity

class PlaceAdapter(
    private val onClick: (PlaceEntity) -> Unit
) : ListAdapter<PlaceEntity, PlaceAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_entity_name)
        val race: TextView = view.findViewById(R.id.tv_entity_race)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = getItem(position)
        holder.name.text = place.name
        holder.race.text = place.placeType
        holder.itemView.setOnClickListener { onClick(place) }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<PlaceEntity>() {
            override fun areItemsTheSame(a: PlaceEntity, b: PlaceEntity) = a.id == b.id
            override fun areContentsTheSame(a: PlaceEntity, b: PlaceEntity) = a == b
        }
    }
}
