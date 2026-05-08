package com.example.criapp.models

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.example.criapp.CriAppDropdown
import com.example.criapp.abstractions.IModel
import com.example.criapp.models.requests.CreateUserRequest
import com.example.criapp.services.DateHelper
import com.example.criapp.models.validators.CreateUserModelValidator

class CreateUserModel(
    val editTextName: EditText,
    val editTextBirthDate: EditText,
    val editTextUsername: EditText,
    val editTextPassword: EditText,
    val editTextNumberOfChildren: EditText,
    val spinnerSex: CriAppDropdown,
    val spinnerEducationalLevel: CriAppDropdown,
    val spinnerFamilyType: CriAppDropdown,
) :IModel<CreateUserModel> {

    override fun validate(errorTextView : TextView, context: Context): Boolean {
        return CreateUserModelValidator(this, errorTextView, context).isValid()
    }

    override fun toRequest(): String {
        return CreateUserRequest(
            editTextName.text.toString(),
            editTextUsername.text.toString(),
            editTextPassword.text.toString(),
            DateHelper.convertDateFromMMDDYYYYtoYYYYMMDD(editTextBirthDate.text.toString()),
            if (editTextNumberOfChildren.text.toString() == "") 0 else editTextNumberOfChildren.text.toString()
                .toInt(),
            spinnerSex.selectedPosition,
            if (spinnerFamilyType.selectedPosition == 0) null else spinnerFamilyType.selectedPosition,
            if (spinnerEducationalLevel.selectedPosition == 0) null else spinnerEducationalLevel.selectedPosition,
        ).toString()
    }
}