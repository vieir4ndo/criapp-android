package com.example.criapp.models.validators

import android.content.Context
import android.widget.TextView
import com.example.criapp.R
import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.CreateForumModel

class CreateForumModelValidator (createForumModel: CreateForumModel, errorTextView : TextView, context: Context
) : AbstractValidator(errorTextView, context) {

    init {
        ruleFor(R.string.name, createForumModel.editTextName, this::isNotEmpty)
        ruleFor(R.string.name, createForumModel.editTextName, this::hasMaxLength100Char)

        ruleFor(R.string.description, createForumModel.editTextDescription, this::isNotEmpty)
        ruleFor(R.string.description, createForumModel.editTextDescription, this::hasMaxLength100Char)
    }
}