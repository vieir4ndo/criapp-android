package com.example.criapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.R
import com.example.criapp.models.responses.AudiovisualResourceResponse

class AudiovisualResourceAdapter(private val data: Array<AudiovisualResourceResponse>) : RecyclerView.Adapter<AudiovisualResourceAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.audiovisual_resource_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.name.text = item.name
        holder.description.text = item.description;
        holder.webView.loadData(item.link, "text/html", "utf-8");
        holder.webView.settings.javaScriptEnabled = true
        holder.webView.webChromeClient = WebChromeClient()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_name)
        val description: TextView = itemView.findViewById(R.id.text_description)
        val webView : WebView = itemView.findViewById(R.id.webview)
    }
}