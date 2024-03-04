package com.example.criapp.models.validators

import android.widget.TextView
import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.ForgotPasswordModel

class ForgotPasswordModelValidator(forgotPasswordModel: ForgotPasswordModel, errorTextView : TextView) : AbstractValidator(errorTextView) {

    init {
        ruleFor("Nombre de usuario", forgotPasswordModel.editTextUsername, this::isNotEmpty)
        ruleFor("Nombre de usuario", forgotPasswordModel.editTextUsername, this::isValidUsername)
    }
}