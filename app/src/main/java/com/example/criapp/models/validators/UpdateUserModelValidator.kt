package com.example.criapp.models.validators

import android.content.Context
import android.widget.TextView
import com.example.criapp.R
import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.UpdateUserModel

class UpdateUserModelValidator(updateUserModel: UpdateUserModel, errorTextView : TextView, context: Context) : AbstractValidator(errorTextView, context) {
    init {
        ruleFor(R.string.name, updateUserModel.editTextName, this::isNotEmpty)
        ruleFor(R.string.name, updateUserModel.editTextName, this::hasMaxLength100Char)

        ruleFor(R.string.birth_date, updateUserModel.editTextBirthDate, this::isNotEmpty)
        ruleFor(R.string.birth_date, updateUserModel.editTextBirthDate, this::isValidDate)

        ruleFor(R.string.sex, updateUserModel.spinnerSex, this::isSpinnerNotEmpty)
    }
}