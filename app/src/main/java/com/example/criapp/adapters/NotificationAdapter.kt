package com.example.criapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.R
import com.example.criapp.models.responses.NotificationResponse
import com.example.criapp.services.DateHelper

class NotificationAdapter(
    private var list: List<NotificationResponse>,
    private val onItemClick: (NotificationResponse) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtType: TextView = view.findViewById(R.id.txtNotificationType)
        val txtTitle: TextView = view.findViewById(R.id.txtNotificationTitle)
        val txtBody: TextView = view.findViewById(R.id.txtNotificationBody)
        val txtDate: TextView = view.findViewById(R.id.txtNotificationDate)
        val dotUnread: View = view.findViewById(R.id.dotUnread)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val ctx = holder.itemView.context

        holder.txtType.text = resolveTypeLabel(item.type, ctx)
        holder.txtTitle.text = item.title
        holder.txtBody.text = item.body
        holder.txtDate.text = DateHelper.formatForDisplay(item.date)
        holder.dotUnread.visibility = if (!item.read) View.VISIBLE else View.INVISIBLE

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    fun updateItem(id: String) {
        val index = list.indexOfFirst { it.id == id }
        if (index >= 0) {
            list = list.toMutableList().also { it[index] = it[index].copy(read = true) }
            notifyItemChanged(index)
        }
    }

    private fun resolveTypeLabel(type: Int, ctx: android.content.Context): String = when (type) {
        1 -> ctx.getString(R.string.notification_type_motivational)
        2 -> ctx.getString(R.string.notification_type_points_risk)
        3 -> ctx.getString(R.string.notification_type_reevaluation)
        4 -> ctx.getString(R.string.notification_type_new_award)
        5 -> ctx.getString(R.string.notification_type_new_recommendations)
        else -> ctx.getString(R.string.notifications)
    }
}
