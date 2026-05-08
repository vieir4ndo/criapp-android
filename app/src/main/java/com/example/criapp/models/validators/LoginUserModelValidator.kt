package com.example.criapp.models.validators

import android.content.Context
import android.widget.TextView
import com.example.criapp.R
import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.LoginUserModel

class LoginUserModelValidator(loginUserModel: LoginUserModel, errorTextView : TextView, context: Context) : AbstractValidator(errorTextView, context) {

    init {
        ruleFor(R.string.field_username, loginUserModel.editTextUsername, this::isNotEmpty)
        ruleFor(R.string.field_username, loginUserModel.editTextUsername, this::isValidUsername)

        ruleFor(R.string.password, loginUserModel.editTextPassword, this::isNotEmpty)
    }
}