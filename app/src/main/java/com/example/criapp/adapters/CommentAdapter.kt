package com.example.criapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.R
import com.example.criapp.models.responses.CommentResponse
import com.example.criapp.services.DateHelper

class CommentAdapter(private val data: Array<CommentResponse>,
                     private val userId : String,
                     private val callBackFunction : ((String) -> Unit)) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        val item = data[position]

        holder.comment.text = item.text
        holder.owner.text = "${item.prcName} ${holder.itemView.context.getString(R.string.commented_colon)}"
        holder.date.text = item.createdAt?.let { DateHelper.formatForDisplayWithTime(it) } ?: ""

        if (item.prcId == userId){
            holder.deleteButtom.setOnClickListener{
                callBackFunction.invoke(item.id)
            }
        }
        else {
            holder.deleteButtom.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val comment: TextView = itemView.findViewById(R.id.text_comment)
        val owner: TextView = itemView.findViewById(R.id.text_owner)
        val date: TextView = itemView.findViewById(R.id.text_date)
        val deleteButtom : ImageButton = itemView.findViewById(R.id.delete_buttom)
    }
}