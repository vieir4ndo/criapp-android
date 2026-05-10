package com.example.criapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.R
import com.example.criapp.models.AdviceResources
import com.example.criapp.models.responses.AdviceResponse

class AdviceAdapter(
    private val list: List<AdviceResponse>
) : RecyclerView.Adapter<AdviceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgAdvice)
        val text: TextView = view.findViewById(R.id.txtAdviceText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.advice_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val index = (item.id - 1).coerceIn(0, AdviceResources.drawables.lastIndex) % AdviceResources.drawables.size
        holder.image.setImageResource(AdviceResources.drawables[index])
        holder.text.text = item.text
    }
}
