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
import com.example.criapp.services.DateHelper

class RecommendationAdapter(
    private val list: List<RecommendationResponse>,
    private val onCheckpoint: (recommendationId: Int, completed: Boolean) -> Unit,
    private val onAlreadyAnswered: () -> Unit = {}
) : RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {

    private var globalEnabled: Boolean = true

    fun setButtonsEnabled(enabled: Boolean) {
        globalEnabled = enabled
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imgRecommendation)
        val text: TextView = view.findViewById(R.id.txtRecommendationText)
        val check1: ImageView = view.findViewById(R.id.check1)
        val check2: ImageView = view.findViewById(R.id.check2)
        val check3: ImageView = view.findViewById(R.id.check3)
        val check4: ImageView = view.findViewById(R.id.check4)
        val date: TextView = view.findViewById(R.id.txtRecDate)
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

        val completed = item.completedCheckpoints.coerceIn(0, 4)
        holder.check1.setImageResource(if (completed >= 1) R.drawable.ic_checkpoint_checked else R.drawable.ic_checkpoint_unchecked)
        holder.check2.setImageResource(if (completed >= 2) R.drawable.ic_checkpoint_checked else R.drawable.ic_checkpoint_unchecked)
        holder.check3.setImageResource(if (completed >= 3) R.drawable.ic_checkpoint_checked else R.drawable.ic_checkpoint_unchecked)
        holder.check4.setImageResource(if (completed >= 4) R.drawable.ic_checkpoint_checked else R.drawable.ic_checkpoint_unchecked)

        holder.date.text = item.createdAt?.let { DateHelper.formatForDisplay(it) } ?: ""

        val b64 = item.image
        if (!b64.isNullOrBlank()) {
            val bytes = Base64.decode(b64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            holder.image.setImageBitmap(bitmap)
        } else {
            holder.image.setImageDrawable(null)
        }

        val alreadyChecked = item.todayCompleted != null

        if (alreadyChecked) {
            val ctx = holder.itemView.context
            if (item.todayCompleted == true) {
                holder.btnYes.isEnabled = true
                holder.btnNo.isEnabled = false
                holder.btnYes.backgroundTintList = android.content.res.ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.btn_yes_active))
                holder.btnNo.backgroundTintList = android.content.res.ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.btn_no_inactive))
            } else {
                holder.btnYes.isEnabled = false
                holder.btnNo.isEnabled = true
                holder.btnYes.backgroundTintList = android.content.res.ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.btn_yes_inactive))
                holder.btnNo.backgroundTintList = android.content.res.ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.btn_no_active))
            }
        } else {
            holder.btnYes.isEnabled = globalEnabled
            holder.btnNo.isEnabled = globalEnabled
        }

        holder.btnYes.setOnClickListener {
            if (!alreadyChecked && globalEnabled) {
                globalEnabled = false
                notifyDataSetChanged()
                onCheckpoint(item.id, true)
            } else if (alreadyChecked) {
                onAlreadyAnswered()
            }
        }
        holder.btnNo.setOnClickListener {
            if (!alreadyChecked && globalEnabled) {
                globalEnabled = false
                notifyDataSetChanged()
                onCheckpoint(item.id, false)
            } else if (alreadyChecked) {
                onAlreadyAnswered()
            }
        }
    }
}
