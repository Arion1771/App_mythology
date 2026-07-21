package com.example.app_mythology.ui.browse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app_mythology.R
import com.example.app_mythology.database.ArtifactEntity

class ArtifactAdapter(
    private val onClick: (ArtifactEntity) -> Unit
) : ListAdapter<ArtifactEntity, ArtifactAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_entity_name)
        val type: TextView = view.findViewById(R.id.tv_entity_race)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artifact = getItem(position)
        holder.name.text = artifact.name
        holder.type.text = artifact.artifactType
        holder.itemView.setOnClickListener { onClick(artifact) }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ArtifactEntity>() {
            override fun areItemsTheSame(a: ArtifactEntity, b: ArtifactEntity) = a.id == b.id
            override fun areContentsTheSame(a: ArtifactEntity, b: ArtifactEntity) = a == b
        }
    }
}
