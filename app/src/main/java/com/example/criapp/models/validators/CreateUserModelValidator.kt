package com.example.criapp.models.validators

import android.widget.TextView
import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.CreateUserModel

class CreateUserModelValidator(
    createUserModel: CreateUserModel, errorTextView : TextView
) : AbstractValidator(errorTextView) {
    init {
        ruleFor("Nombre", createUserModel.editTextName, this::isNotEmpty)
        ruleFor("Nombre", createUserModel.editTextName, this::hasMaxLength100Char)

        ruleFor("Nombre de usuario", createUserModel.editTextUsername, this::isNotEmpty)
        ruleFor("Nombre de usuario", createUserModel.editTextUsername, this::isValidUsername)

        ruleFor("Fecha de nacimiento", createUserModel.editTextBirthDate, this::isNotEmpty)
        ruleFor("Fecha de nacimiento", createUserModel.editTextBirthDate, this::isValidDate)

        ruleFor("Contraseña", createUserModel.editTextPassword, this::isNotEmpty)

        ruleFor("Sexo", createUserModel.spinnerSex, this::isSpinnerNotEmpty)
    }
}