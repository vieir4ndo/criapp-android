package com.example.criapp.models

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.example.criapp.CriAppDropdown
import com.example.criapp.abstractions.IModel
import com.example.criapp.models.requests.UpdateUserRequest
import com.example.criapp.models.validators.UpdateUserModelValidator
import com.example.criapp.services.DateHelper

class UpdateUserModel(
    val editTextName: EditText,
    val editTextBirthDate: EditText,
    val spinnerSex: CriAppDropdown,
    val spinnerEducationalLevel: CriAppDropdown,
    val spinnerFamilyType: CriAppDropdown,
    val editTextNumberOfChildren: EditText
) : IModel<UpdateUserModel> {
    override fun validate(errorTextView : TextView, context: Context): Boolean {
        return UpdateUserModelValidator(this, errorTextView, context).isValid();
    }

    override fun toRequest(): String {
        return UpdateUserRequest(
            editTextName.text.toString(),
            DateHelper.convertDateFromMMDDYYYYtoYYYYMMDD(editTextBirthDate.text.toString()),
            spinnerSex.selectedPosition,
            if (spinnerFamilyType.selectedPosition == 0) null else spinnerFamilyType.selectedPosition,
            if (spinnerEducationalLevel.selectedPosition == 0) null else spinnerEducationalLevel.selectedPosition,
            if (editTextNumberOfChildren.text.toString() == "") 0 else editTextNumberOfChildren.text.toString()
                .toInt(),
        ).toString()
    }

}