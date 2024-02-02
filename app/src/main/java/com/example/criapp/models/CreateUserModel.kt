package com.example.criapp.models

import android.widget.EditText
import android.widget.Spinner
import com.example.criapp.abstractions.IModel
import com.example.criapp.models.requests.CreateUserRequest
import com.example.criapp.services.DateHelper
import com.example.criapp.validators.CreateUserModelValidator

class CreateUserModel(
    val editTextName: EditText,
    val editTextBirthDate: EditText,
    val editTextUsername: EditText,
    val editTextPassword: EditText,
    val editTextNumberOfChildren: EditText,
    val spinnerSex: Spinner,
    val spinnerEducationalLevel: Spinner,
    val spinnerFamilyType: Spinner,
) :IModel<CreateUserModel> {

    override fun validate(): Boolean {
        return CreateUserModelValidator(this).isValid()
    }

    fun toRequest(sex: Int, familyType: Int, educationalLevel: Int): String {
        val date =
            DateHelper.getDayMonthAndYearFromFormattedDateTest(editTextBirthDate.text.toString());

        return CreateUserRequest(
            editTextName.text.toString(),
            editTextUsername.text.toString(),
            editTextPassword.text.toString(),
            DateHelper.formatDateYYYYMMDD(date.first, date.second, date.third),
            if (editTextNumberOfChildren.text.toString() == "") 0 else editTextNumberOfChildren.text.toString()
                .toInt(),
            sex,
            familyType,
            educationalLevel
        ).toString()
    }
}