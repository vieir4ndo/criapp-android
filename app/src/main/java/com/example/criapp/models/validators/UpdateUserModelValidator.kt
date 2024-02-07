package com.example.criapp.models.validators

import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.UpdateUserModel

class UpdateUserModelValidator(updateUserModel: UpdateUserModel) : AbstractValidator() {
    init {
        ruleFor("Nombre", updateUserModel.editTextName, this::isNotEmpty)
        ruleFor("Nombre", updateUserModel.editTextName, this::hasMaxLength100Char)

        ruleFor("Fecha de nacimiento", updateUserModel.editTextBirthDate, this::isNotEmpty)
        ruleFor("Fecha de nacimiento", updateUserModel.editTextBirthDate, this::isValidDate)

        ruleFor("Sexo", updateUserModel.spinnerSex, this::isSpinnerNotEmpty)
    }
}