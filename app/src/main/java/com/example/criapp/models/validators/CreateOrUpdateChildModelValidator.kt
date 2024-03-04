package com.example.criapp.models.validators

import android.widget.TextView
import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.CreateOrUpdateChildModel

class CreateOrUpdateChildModelValidator(createOrUpdateChildModel : CreateOrUpdateChildModel, errorTextView : TextView) : AbstractValidator(errorTextView) {

    init {
        ruleFor("Nombre", createOrUpdateChildModel.editTextName, this::isNotEmpty)
        ruleFor("Nombre", createOrUpdateChildModel.editTextName, this::hasMaxLength100Char)

        ruleFor("Fecha de nacimiento", createOrUpdateChildModel.editTextBirthDate, this::isNotEmpty)
        ruleFor("Fecha de nacimiento", createOrUpdateChildModel.editTextBirthDate, this::isValidDate)

        ruleFor("Sexo", createOrUpdateChildModel.spinnerSex, this::isSpinnerNotEmpty)

        ruleFor("Numero de meses de lactancia", createOrUpdateChildModel.editTextMonthsOfBreastFeeding, this::isNotEmpty)

        ruleFor("Prematuro", createOrUpdateChildModel.spinnerIsPremature, this::isSpinnerNotEmpty)
    }
}