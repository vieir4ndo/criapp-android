package com.example.criapp.models.validators

import android.content.Context
import android.widget.TextView
import com.example.criapp.R
import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.CreateUserModel

class CreateUserModelValidator(
    createUserModel: CreateUserModel, errorTextView : TextView, context: Context
) : AbstractValidator(errorTextView, context) {
    init {
        ruleFor(R.string.name, createUserModel.editTextName, this::isNotEmpty)
        ruleFor(R.string.name, createUserModel.editTextName, this::hasMaxLength100Char)

        ruleFor(R.string.field_username, createUserModel.editTextUsername, this::isNotEmpty)
        ruleFor(R.string.field_username, createUserModel.editTextUsername, this::isValidUsername)

        ruleFor(R.string.birth_date, createUserModel.editTextBirthDate, this::isNotEmpty)
        ruleFor(R.string.birth_date, createUserModel.editTextBirthDate, this::isValidDate)

        ruleFor(R.string.password, createUserModel.editTextPassword, this::isNotEmpty)

        ruleFor(R.string.sex, createUserModel.spinnerSex, this::isSpinnerNotEmpty)
    }
}