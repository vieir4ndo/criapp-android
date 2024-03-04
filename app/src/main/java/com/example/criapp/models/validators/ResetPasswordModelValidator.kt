package com.example.criapp.models.validators

import android.widget.TextView
import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.ResetPasswordModel

class ResetPasswordModelValidator(resetPasswordModel: ResetPasswordModel, errorTextView : TextView) : AbstractValidator(errorTextView) {

    init {
        ruleFor("Contraseña Nueva", resetPasswordModel.editTextNewPassword, this::isNotEmpty)

        ruleFor("Código", resetPasswordModel.editTextCode, this::isNotEmpty)
    }
}