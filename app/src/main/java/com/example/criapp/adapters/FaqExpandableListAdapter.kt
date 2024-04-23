package com.example.criapp.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.criapp.R

class FaqExpandableListAdapter(var context: Context, var faqData : List<Pair<String, String>>)  : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int = faqData.size

    override fun getChildrenCount(groupPosition: Int): Int = 1

    override fun getGroup(groupPosition: Int): Any = faqData[groupPosition].first

    override fun getChild(groupPosition: Int, childPosition: Int): Any = faqData[groupPosition].second

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = true

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_expandable_list_item_1, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.setTextColor(ContextCompat.getColor(context, R.color.white))
        textView.textSize = 20f
        textView.text = getGroup(groupPosition).toString()
        textView.setTypeface(null, Typeface.BOLD)
        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.setTextColor(ContextCompat.getColor(context, R.color.white))
        textView.text = getChild(groupPosition, childPosition).toString()
        textView.textSize = 15f
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = false
}