package com.example.criapp.models.validators

import android.content.Context
import android.widget.TextView
import com.example.criapp.R
import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.ResetPasswordModel

class ResetPasswordModelValidator(resetPasswordModel: ResetPasswordModel, errorTextView : TextView, context: Context) : AbstractValidator(errorTextView, context) {

    init {
        ruleFor(R.string.new_password, resetPasswordModel.editTextNewPassword, this::isNotEmpty)

        ruleFor(R.string.code, resetPasswordModel.editTextCode, this::isNotEmpty)
    }
}