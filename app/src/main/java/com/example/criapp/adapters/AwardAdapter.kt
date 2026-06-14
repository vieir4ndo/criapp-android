package com.example.criapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.R
import com.example.criapp.models.responses.AwardResponse
import com.example.criapp.services.DateHelper

class AwardAdapter(
    private val list: List<AwardResponse>
) : RecyclerView.Adapter<AwardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.txtTitle)
        val category = view.findViewById<TextView>(R.id.txtCategory)
        val description = view.findViewById<TextView>(R.id.txtDescription)
        val image = view.findViewById<ImageView>(R.id.imgAward)
        val date = view.findViewById<TextView>(R.id.txtAwardDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.award_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.title.text = item.text
        holder.category.visibility = android.view.View.GONE
        holder.description.text = item.description ?: ""
        holder.date.text = item.createdAt?.let { DateHelper.formatForDisplay(it) } ?: ""

        val drawableRes = when (item.type) {
            1 -> R.drawable.habit_builder_award
            2 -> R.drawable.consistent_parent_award
            3 -> R.drawable.silver_cognitive_award
            4 -> R.drawable.silver_emotional_award
            5 -> R.drawable.silver_routines_award
            6 -> R.drawable.silver_social_award
            7 -> R.drawable.gold_parent_award
            else -> null
        }
        if (drawableRes != null) holder.image.setImageResource(drawableRes) else holder.image.setImageDrawable(null)
    }
}