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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.audiovisual_resource_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.name.text = item.name
        holder.description.text = item.description

        val videoId = Uri.parse(item.iframe).getQueryParameter("v")
        if (videoId != null) {
            Glide.with(holder.thumbnail)
                .load("https://img.youtube.com/vi/$videoId/hqdefault.jpg")
                .into(holder.thumbnail)
        }

        holder.thumbnail.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.iframe))
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_name)
        val description: TextView = itemView.findViewById(R.id.text_description)
        val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
    }
}