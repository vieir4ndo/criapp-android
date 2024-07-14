package com.example.criapp.models.validators

import android.widget.TextView
import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.CreateCommentModel

class CreateCommentModelValidator(
    createCommentModel: CreateCommentModel, errorTextView : TextView
) : AbstractValidator(errorTextView) {

    init {
        ruleFor("Texto", createCommentModel.editTextText, this::isNotEmpty)
        ruleFor("Texto", createCommentModel.editTextText, this::hasMaxLength100Char)
    }
}