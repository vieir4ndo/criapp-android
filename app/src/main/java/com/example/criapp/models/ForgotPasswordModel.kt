package com.example.criapp.models

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.example.criapp.abstractions.IModel
import com.example.criapp.models.requests.ForgotPasswordRequest
import com.example.criapp.models.validators.ForgotPasswordModelValidator

class ForgotPasswordModel(
    val editTextUsername: EditText
) : IModel<ForgotPasswordModel> {
    override fun validate(errorTextView : TextView, context: Context): Boolean {
        return ForgotPasswordModelValidator(this, errorTextView, context).isValid()
    }

    override fun toRequest(): String {
        return ForgotPasswordRequest(editTextUsername.text.toString()).toString()
    }

}