package com.example.criapp.abstractions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.criapp.R

class CriAppSpinnerAdapter(
    private val context: Context,
    private val data: List<String>
) : BaseAdapter() {

    var position: Int = 0

    override fun getCount(): Int = data.size

    override fun getItem(position: Int): Any = data[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.criapp_spinner_layout, parent, false)
        view.findViewById<TextView>(android.R.id.text1).text = data[position]
        return view
    }
}
