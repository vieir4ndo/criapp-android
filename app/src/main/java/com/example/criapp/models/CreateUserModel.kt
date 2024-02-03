package com.example.criapp.models

import android.widget.EditText
import android.widget.Spinner
import com.example.criapp.abstractions.CriAppSpinnerAdapter
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
    val spinnerSex: Spinner,
    val spinnerEducationalLevel: Spinner,
    val spinnerFamilyType: Spinner,
) :IModel<CreateUserModel> {

    override fun validate(): Boolean {
        return CreateUserModelValidator(this).isValid()
    }

    fun toRequest(sex: Int, familyType: Int, educationalLevel: Int): String {

        val sexAdapter = spinnerSex.getAdapter() as CriAppSpinnerAdapter;

        return CreateUserRequest(
            editTextName.text.toString(),
            editTextUsername.text.toString(),
            editTextPassword.text.toString(),
            DateHelper.convertDateFromMMDDYYYYtoYYYYMMDD(editTextBirthDate.text.toString()),
            if (editTextNumberOfChildren.text.toString() == "") 0 else editTextNumberOfChildren.text.toString()
                .toInt(),
            sexAdapter.position,
            if (familyType == 0) null else familyType,
            if (educationalLevel == 0) null else educationalLevel,
        ).toString()
    }
}