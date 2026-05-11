package com.example.criapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.R
import com.example.criapp.models.responses.NotificationResponse
import com.example.criapp.services.DateHelper

class NotificationAdapter(
    private var list: List<NotificationResponse>,
    private val onItemClick: (NotificationResponse) -> Unit,
    private val onLoadMoreClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class FooterState { HIDDEN, BUTTON, LOADING }

    private var footerState = FooterState.HIDDEN

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_FOOTER = 1
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtType: TextView = view.findViewById(R.id.txtNotificationType)
        val txtTitle: TextView = view.findViewById(R.id.txtNotificationTitle)
        val txtBody: TextView = view.findViewById(R.id.txtNotificationBody)
        val txtDate: TextView = view.findViewById(R.id.txtNotificationDate)
        val dotUnread: View = view.findViewById(R.id.dotUnread)
    }

    class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btnLoadMore: TextView = view.findViewById(R.id.btnLoadMore)
        val progressLoadMore: ProgressBar = view.findViewById(R.id.progressLoadMore)
    }

    override fun getItemViewType(position: Int): Int =
        if (footerState != FooterState.HIDDEN && position == list.size) VIEW_TYPE_FOOTER else VIEW_TYPE_ITEM

    override fun getItemCount() = list.size + if (footerState != FooterState.HIDDEN) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_FOOTER) {
            FooterViewHolder(inflater.inflate(R.layout.notification_footer_layout, parent, false))
        } else {
            ItemViewHolder(inflater.inflate(R.layout.notification_item_layout, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                val item = list[position]
                val ctx = holder.itemView.context
                holder.txtType.text = resolveTypeLabel(item.type, ctx)
                holder.txtTitle.text = item.title
                holder.txtBody.text = item.body
                holder.txtDate.text = DateHelper.formatForDisplay(item.date)
                holder.dotUnread.visibility = if (!item.read) View.VISIBLE else View.INVISIBLE
                holder.itemView.setOnClickListener { onItemClick(item) }
            }
            is FooterViewHolder -> {
                holder.btnLoadMore.visibility = if (footerState == FooterState.BUTTON) View.VISIBLE else View.GONE
                holder.progressLoadMore.visibility = if (footerState == FooterState.LOADING) View.VISIBLE else View.GONE
                holder.btnLoadMore.setOnClickListener { onLoadMoreClick() }
            }
        }
    }

    fun setFooterState(state: FooterState) {
        val wasHidden = footerState == FooterState.HIDDEN
        val isHidden = state == FooterState.HIDDEN
        footerState = state
        val footerPos = list.size
        when {
            wasHidden && !isHidden -> notifyItemInserted(footerPos)
            !wasHidden && isHidden -> notifyItemRemoved(footerPos)
            else -> notifyItemChanged(footerPos)
        }
    }

    fun updateItem(id: String) {
        val index = list.indexOfFirst { it.id == id }
        if (index >= 0) {
            list = list.toMutableList().also { it[index] = it[index].copy(read = true) }
            notifyItemChanged(index)
        }
    }

    fun appendItems(newItems: List<NotificationResponse>) {
        val startPos = list.size
        list = list + newItems
        notifyItemRangeInserted(startPos, newItems.size)
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
