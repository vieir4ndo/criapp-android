package com.example.criapp.services

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.criapp.abstractions.CriAppSpinnerAdapter
import com.google.android.material.textfield.TextInputLayout

class SpinnerHelper {
    companion object {

        val sexOptions = listOf("Seleccionar", "Hombre", "Mujer", "Otro");
        val educationalLevelOptions = listOf("Seleccionar", "Sin estudios", "Primaria/ESO", "Bachillerato", "Formación profesional", "Estudios universitarios")
        val familyTypeOptions = listOf("Seleccionar",
            "Monoparental femenina (mujer sola con niñas o niños a cargo)",
            "Monoparental masculina (hombre sólo con niñas o niños a cargo)",
            "Pareja de hombre y mujer con niñas o niños a cargo",
            "Pareja de hombre y hombre con niñas o niños a cargo",
            "Pareja de mujer y mujer con niñas o niños a cargo");
        val yesOrNoOptions = listOf("Seleccionar", "Sí", "No");

        fun configSpinner(
            context: Context,
            spinner: Spinner,
            options: List<String>,
            onSelected: (Int) -> Unit,
            onNothingSelected: (() -> Unit)? = null
        ) {
            val arrayAdapter = ArrayAdapter<String>(
                context,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item
            )
            arrayAdapter.addAll(options)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    onSelected(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    if (onNothingSelected != null) onNothingSelected()
                }
            }

            spinner.adapter = arrayAdapter
        }

        fun configCriAppSpinner(
            context: Context,
            spinner: Spinner,
            options: List<String>,
            inputLayout: TextInputLayout
        ) {

            val arrayAdapter = CriAppSpinnerAdapter(context, options)

            arrayAdapter.inputLayout = inputLayout

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    arrayAdapter.position = position;
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    arrayAdapter.position = 0;
                }
            }

            spinner.adapter = arrayAdapter
        }
    }
}