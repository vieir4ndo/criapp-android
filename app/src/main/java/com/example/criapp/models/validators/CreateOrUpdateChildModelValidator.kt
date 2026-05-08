package com.example.criapp.models.validators

import android.content.Context
import android.widget.TextView
import com.example.criapp.R
import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.CreateOrUpdateChildModel

class CreateOrUpdateChildModelValidator(createOrUpdateChildModel : CreateOrUpdateChildModel, errorTextView : TextView, context: Context) : AbstractValidator(errorTextView, context) {

    init {
        ruleFor(R.string.name, createOrUpdateChildModel.editTextName, this::isNotEmpty)
        ruleFor(R.string.name, createOrUpdateChildModel.editTextName, this::hasMaxLength100Char)

        ruleFor(R.string.birth_date, createOrUpdateChildModel.editTextBirthDate, this::isNotEmpty)
        ruleFor(R.string.birth_date, createOrUpdateChildModel.editTextBirthDate, this::isValidDate)

        ruleFor(R.string.sex, createOrUpdateChildModel.spinnerSex, this::isSpinnerNotEmpty)

        ruleFor(R.string.months_of_breast_feeding, createOrUpdateChildModel.editTextMonthsOfBreastFeeding, this::isNotEmpty)

        ruleFor(R.string.premature, createOrUpdateChildModel.spinnerIsPremature, this::isSpinnerNotEmpty)
    }
}