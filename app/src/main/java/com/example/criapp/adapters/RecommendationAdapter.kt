package com.example.criapp.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.R
import com.example.criapp.models.responses.RecommendationResponse

class RecommendationAdapter(
    private val list: List<RecommendationResponse>
) : RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgRecommendation)
        val text: TextView = view.findViewById(R.id.txtRecommendationText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recommendation_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.text.text = item.text

        val b64 = item.image
        if (!b64.isNullOrBlank()) {
            val bytes = Base64.decode(b64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            holder.image.setImageBitmap(bitmap)
        } else {
            holder.image.setImageDrawable(null)
        }
    }
}
