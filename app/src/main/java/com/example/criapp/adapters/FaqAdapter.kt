package com.example.criapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.R

class FaqAdapter(private val data: List<Pair<String, String>>)  : RecyclerView.Adapter<FaqAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.faq_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FaqAdapter.ViewHolder, position: Int) {
        val item = data[position]

        holder.question.text = item.first;
        holder.answer.text = item.second;
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val question: TextView = itemView.findViewById(R.id.text_question)
        val answer: TextView = itemView.findViewById(R.id.text_answer)
    }
}