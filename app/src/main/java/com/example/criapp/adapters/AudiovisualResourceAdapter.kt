package com.example.criapp.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.criapp.R
import com.example.criapp.models.responses.AudiovisualResourceResponse

class AudiovisualResourceAdapter(
    private val data: Array<AudiovisualResourceResponse>
) : RecyclerView.Adapter<AudiovisualResourceAdapter.ViewHolder>() {

    private var expandedPosition = RecyclerView.NO_ID.toInt()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.audiovisual_resource_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.name.text = item.name
        holder.description.text = item.description

        val isExpanded = position == expandedPosition
        holder.description.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.thumbnailContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.divider.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.chevron.rotation = if (isExpanded) 180f else 0f

        val link = item.link
        if (!link.isNullOrEmpty()) {
            val videoId = Uri.parse(link).getQueryParameter("v")
            if (videoId != null) {
                Glide.with(holder.thumbnail)
                    .load("https://img.youtube.com/vi/$videoId/hqdefault.jpg")
                    .into(holder.thumbnail)
            }
            holder.thumbnailContainer.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                it.context.startActivity(intent)
            }
        } else {
            holder.thumbnailContainer.setOnClickListener(null)
        }

        holder.itemView.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos == RecyclerView.NO_ID.toInt()) return@setOnClickListener
            val previousExpanded = expandedPosition
            expandedPosition = if (pos == expandedPosition) RecyclerView.NO_ID.toInt() else pos
            if (previousExpanded != RecyclerView.NO_ID.toInt()) notifyItemChanged(previousExpanded)
            notifyItemChanged(pos)
        }
    }

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_name)
        val description: TextView = itemView.findViewById(R.id.text_description)
        val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
        val thumbnailContainer: View = itemView.findViewById(R.id.thumbnail_container)
        val chevron: ImageView = itemView.findViewById(R.id.chevron)
        val divider: View = itemView.findViewById(R.id.divider)
    }
}