package com.example.criapp.models.validators

import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.ResetPasswordModel

class ResetPasswordModelValidator(resetPasswordModel: ResetPasswordModel) : AbstractValidator() {

    init {
        ruleFor("Contraseña Nueva", resetPasswordModel.editTextNewPassword, this::isNotEmpty)

        ruleFor("Código", resetPasswordModel.editTextCode, this::isNotEmpty)
    }
}