package com.example.criapp.models

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.example.criapp.CriAppDropdown
import com.example.criapp.abstractions.IModel
import com.example.criapp.models.requests.CreateOrUpdateChildRequest
import com.example.criapp.models.validators.CreateOrUpdateChildModelValidator
import com.example.criapp.services.DateHelper

class CreateOrUpdateChildModel(
    val editTextName: EditText,
    val editTextBirthDate: EditText,
    val spinnerSex: CriAppDropdown,
    val editTextMonthsOfBreastFeeding: EditText,
    val spinnerIsPremature: CriAppDropdown,
) : IModel<CreateOrUpdateChildModel> {
    override fun validate(errorTextView: TextView, context: Context): Boolean {
        return CreateOrUpdateChildModelValidator(this, errorTextView, context).isValid()
    }

    override fun toRequest(): String {
        return CreateOrUpdateChildRequest(
            editTextName.text.toString(),
            DateHelper.convertDateFromMMDDYYYYtoYYYYMMDD(editTextBirthDate.text.toString()),
            spinnerSex.selectedPosition,
            if (editTextMonthsOfBreastFeeding.text.toString() == "") 0 else editTextMonthsOfBreastFeeding.text.toString().toInt(),
            when (spinnerIsPremature.selectedPosition) {
                1 -> true;
                else -> false
            }
        ).toString()
    }

}