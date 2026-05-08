package com.example.criapp.models

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.example.criapp.abstractions.IModel
import com.example.criapp.models.requests.CreateForumRequest
import com.example.criapp.models.validators.CreateForumModelValidator

class CreateForumModel(
    val editTextName: EditText,
    val editTextDescription: EditText,
) : IModel<CreateForumModel>
{
    override fun validate(errorTextView: TextView, context: Context): Boolean {
        return CreateForumModelValidator(this, errorTextView, context).isValid()
    }

    override fun toRequest(): String {
        return CreateForumRequest(
            editTextName.text.toString(),
            editTextDescription.text.toString(),
        ).toString()
    }
}