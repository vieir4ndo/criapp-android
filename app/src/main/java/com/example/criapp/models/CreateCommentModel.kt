package com.example.criapp.models

import android.widget.EditText
import android.widget.TextView
import com.example.criapp.abstractions.IModel
import com.example.criapp.models.requests.CreateCommentRequest
import com.example.criapp.models.validators.CreateCommentModelValidator

class CreateCommentModel(
    val editTextText: EditText,
    val forumId: String,
) : IModel<CreateCommentModel> {
    override fun validate(errorTextView: TextView): Boolean {
        return CreateCommentModelValidator(this, errorTextView).isValid()
    }

    override fun toRequest(): String {
        return CreateCommentRequest(forumId, editTextText.text.toString()).toString()
    }
}