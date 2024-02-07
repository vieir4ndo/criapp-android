package com.example.criapp.models.validators

import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.ForgotPasswordModel

class ForgotPasswordModelValidator(forgotPasswordModel: ForgotPasswordModel) : AbstractValidator() {

    init {
        ruleFor("Nombre de usuario", forgotPasswordModel.editTextUsername, this::isNotEmpty)
        ruleFor("Nombre de usuario", forgotPasswordModel.editTextUsername, this::isValidUsername)
    }
}