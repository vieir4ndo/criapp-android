package com.example.criapp.models.validators

import android.widget.TextView
import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.CreateForumModel

class CreateForumModelValidator (createForumModel: CreateForumModel, errorTextView : TextView
) : AbstractValidator(errorTextView) {

    init {
        ruleFor("Nombre", createForumModel.editTextName, this::isNotEmpty)
        ruleFor("Nombre", createForumModel.editTextName, this::hasMaxLength100Char)

        ruleFor("Descripción", createForumModel.editTextDescription, this::isNotEmpty)
        ruleFor("Descripción", createForumModel.editTextDescription, this::hasMaxLength100Char)
    }
}