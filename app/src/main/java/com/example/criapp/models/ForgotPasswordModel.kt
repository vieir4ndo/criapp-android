package com.example.criapp.models

import android.widget.EditText
import com.example.criapp.abstractions.IModel
import com.example.criapp.models.requests.ForgotPasswordRequest
import com.example.criapp.models.validators.ForgotPasswordModelValidator

class ForgotPasswordModel(
    val editTextUsername: EditText
) : IModel<ForgotPasswordModel> {
    override fun validate(): Boolean {
        return ForgotPasswordModelValidator(this).isValid()
    }

    override fun toRequest(): String {
        return ForgotPasswordRequest(editTextUsername.text.toString()).toString()
    }

}