package com.example.criapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.R
import com.example.criapp.models.responses.FaqResponse

class FaqAdapter(private val data: Array<FaqResponse>) : RecyclerView.Adapter<FaqAdapter.ViewHolder>() {

    private val expandedStates = BooleanArray(data.size) { false }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.faq_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.question.text = item.question
        holder.answer.text = item.answer

        val isExpanded = expandedStates[position]
        holder.answer.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.divider.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.chevron.rotation = if (isExpanded) 180f else 0f

        holder.itemView.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos == RecyclerView.NO_ID.toInt()) return@setOnClickListener
            expandedStates[pos] = !expandedStates[pos]
            notifyItemChanged(pos)
        }
    }

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val question: TextView = itemView.findViewById(R.id.text_question)
        val answer: TextView = itemView.findViewById(R.id.text_answer)
        val chevron: ImageView = itemView.findViewById(R.id.chevron)
        val divider: View = itemView.findViewById(R.id.divider)
    }
}