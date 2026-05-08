package com.example.criapp.models.validators

import android.content.Context
import android.widget.TextView
import com.example.criapp.R
import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.ForgotPasswordModel

class ForgotPasswordModelValidator(forgotPasswordModel: ForgotPasswordModel, errorTextView : TextView, context: Context) : AbstractValidator(errorTextView, context) {

    init {
        ruleFor(R.string.field_username, forgotPasswordModel.editTextUsername, this::isNotEmpty)
        ruleFor(R.string.field_username, forgotPasswordModel.editTextUsername, this::isValidUsername)
    }
}