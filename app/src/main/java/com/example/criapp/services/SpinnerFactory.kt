package com.example.criapp.services

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

class SpinnerFactory {
    fun configSpinner(context: Context, spinner: Spinner, options: List<String>, onSelected : (Int) -> Unit, onNothingSelected: () -> Unit) {
        val arrayAdapter = ArrayAdapter<String>(context, com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
        arrayAdapter.addAll(options)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onSelected(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                onNothingSelected()
            }
        }

        spinner.adapter = arrayAdapter
    }
}