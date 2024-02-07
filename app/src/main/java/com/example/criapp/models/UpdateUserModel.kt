package com.example.criapp.models

import android.widget.EditText
import android.widget.Spinner
import com.example.criapp.abstractions.CriAppSpinnerAdapter
import com.example.criapp.abstractions.IModel
import com.example.criapp.models.requests.UpdateUserRequest
import com.example.criapp.models.validators.UpdateUserModelValidator
import com.example.criapp.services.DateHelper

class UpdateUserModel(
    val editTextName: EditText,
    val editTextBirthDate: EditText,
    val spinnerSex: Spinner,
    val spinnerEducationalLevel: Spinner,
    val spinnerFamilyType: Spinner,
    val editTextNumberOfChildren: EditText
) : IModel<UpdateUserModel> {
    override fun validate(): Boolean {
        return UpdateUserModelValidator(this).isValid();
    }

    override fun toRequest(): String {
        val sexAdapter = spinnerSex.getAdapter() as CriAppSpinnerAdapter;
        val familyTypeAdapter = spinnerFamilyType.getAdapter() as CriAppSpinnerAdapter;
        val educationalLevelAdapter = spinnerEducationalLevel.getAdapter() as CriAppSpinnerAdapter;

        return UpdateUserRequest(
            editTextName.text.toString(),
            DateHelper.convertDateFromMMDDYYYYtoYYYYMMDD(editTextBirthDate.text.toString()),
            sexAdapter.position,
            if (familyTypeAdapter.position == 0) null else familyTypeAdapter.position,
            if (educationalLevelAdapter.position == 0) null else educationalLevelAdapter.position,
            if (editTextNumberOfChildren.text.toString() == "") 0 else editTextNumberOfChildren.text.toString()
                .toInt(),
        ).toString()
    }

}