package com.example.criapp.models

import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.example.criapp.abstractions.CriAppSpinnerAdapter
import com.example.criapp.abstractions.IModel
import com.example.criapp.models.requests.CreateOrUpdateChildRequest
import com.example.criapp.models.validators.CreateOrUpdateChildModelValidator
import com.example.criapp.services.DateHelper

class CreateOrUpdateChildModel(
    val editTextName: EditText,
    val editTextBirthDate: EditText,
    val spinnerSex: Spinner,
    val editTextMonthsOfBreastFeeding: EditText,
    val spinnerIsPremature: Spinner,
) : IModel<CreateOrUpdateChildModel> {
    override fun validate(errorTextView: TextView): Boolean {
        return CreateOrUpdateChildModelValidator(this, errorTextView).isValid()
    }

    override fun toRequest(): String {
        val sexAdapter = spinnerSex.getAdapter() as CriAppSpinnerAdapter;
        val prematureAdapter = spinnerIsPremature.getAdapter() as CriAppSpinnerAdapter;

        return CreateOrUpdateChildRequest(
            editTextName.text.toString(),
            DateHelper.convertDateFromMMDDYYYYtoYYYYMMDD(editTextBirthDate.text.toString()),
            sexAdapter.position,
            if (editTextMonthsOfBreastFeeding.text.toString() == "") 0 else editTextMonthsOfBreastFeeding.text.toString().toInt(),
            when (prematureAdapter.position) {
                1 -> true;
                else -> false
            }
        ).toString()
    }

}