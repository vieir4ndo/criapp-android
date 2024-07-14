package com.example.criapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.R
import com.example.criapp.models.responses.ForumResponse

class ForumAdapter(private val data: Array<ForumResponse>,
                   private val userId : String,
                   private val redirectView: ((String) -> Unit),
                   private val redirectUpdate : ((String) -> Unit)) : RecyclerView.Adapter<ForumAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forum_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.name.text = item.name
        holder.owner.text = item.prcName;

        holder.card.setOnClickListener{
            redirectView.invoke(item.toString())
        }

        if (item.prcId == userId) {
            holder.update.setOnClickListener{
                redirectUpdate.invoke(item.toString())
            }
        }
        else {
            holder.update.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: View = itemView.findViewById(R.id.main_card)
        val name: TextView = itemView.findViewById(R.id.forum_name)
        val owner: TextView = itemView.findViewById(R.id.forum_owner)
        val update : ImageButton = itemView.findViewById(R.id.edit_buttom)
    }
}