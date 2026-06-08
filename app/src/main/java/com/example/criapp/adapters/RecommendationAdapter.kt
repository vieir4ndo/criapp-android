package com.example.criapp.adapters

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.R
import com.example.criapp.models.responses.RecommendationResponse

class RecommendationAdapter(
    private val list: List<RecommendationResponse>,
    private val onCheckpoint: (recommendationId: Int, completed: Boolean) -> Unit
) : RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {

    private var globalEnabled: Boolean = true

    fun setButtonsEnabled(enabled: Boolean) {
        globalEnabled = enabled
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgRecommendation)
        val text: TextView = view.findViewById(R.id.txtRecommendationText)
        val btnYes: Button = view.findViewById(R.id.btnYes)
        val btnNo: Button = view.findViewById(R.id.btnNo)
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

        val alreadyChecked = item.todayCompleted != null
        val interactive = globalEnabled && !alreadyChecked
        holder.btnYes.isEnabled = interactive
        holder.btnNo.isEnabled = interactive

        if (alreadyChecked) {
            val ctx = holder.itemView.context
            val completedColor = ContextCompat.getColor(ctx, R.color.checkpoint_done)
            val pendingColor = ContextCompat.getColor(ctx, R.color.checkpoint_pending)
            if (item.todayCompleted == true) {
                holder.btnYes.backgroundTintList = android.content.res.ColorStateList.valueOf(completedColor)
                holder.btnNo.backgroundTintList = android.content.res.ColorStateList.valueOf(pendingColor)
            } else {
                holder.btnYes.backgroundTintList = android.content.res.ColorStateList.valueOf(pendingColor)
                holder.btnNo.backgroundTintList = android.content.res.ColorStateList.valueOf(completedColor)
            }
        }

        holder.btnYes.setOnClickListener {
            if (!alreadyChecked && globalEnabled) {
                globalEnabled = false
                notifyDataSetChanged()
                onCheckpoint(item.id, true)
            }
        }
        holder.btnNo.setOnClickListener {
            if (!alreadyChecked && globalEnabled) {
                globalEnabled = false
                notifyDataSetChanged()
                onCheckpoint(item.id, false)
            }
        }
    }
}
