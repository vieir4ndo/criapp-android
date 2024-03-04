package com.example.criapp.services

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import com.example.criapp.abstractions.CriAppSpinnerAdapter

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

        fun configCriAppSpinner(
            context: Context,
            spinner: Spinner,
            options: List<String>,
            errorTextView: TextView
        ) {

            val arrayAdapter = CriAppSpinnerAdapter(context, options)

            arrayAdapter.errorTextView = errorTextView

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    arrayAdapter.position = position;
                    arrayAdapter.errorTextView.error = null;
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    arrayAdapter.position = 0;
                }
            }

            spinner.adapter = arrayAdapter
        }
    }
}