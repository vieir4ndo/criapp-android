package com.example.criapp.models.validators

import android.widget.TextView
import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.UpdateUserModel

class UpdateUserModelValidator(updateUserModel: UpdateUserModel, errorTextView : TextView) : AbstractValidator(errorTextView) {
    init {
        ruleFor("Nombre", updateUserModel.editTextName, this::isNotEmpty)
        ruleFor("Nombre", updateUserModel.editTextName, this::hasMaxLength100Char)

        ruleFor("Fecha de nacimiento", updateUserModel.editTextBirthDate, this::isNotEmpty)
        ruleFor("Fecha de nacimiento", updateUserModel.editTextBirthDate, this::isValidDate)

        ruleFor("Sexo", updateUserModel.spinnerSex, this::isSpinnerNotEmpty)
    }
}