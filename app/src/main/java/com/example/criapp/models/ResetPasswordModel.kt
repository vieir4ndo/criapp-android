package com.example.criapp.models

import android.widget.EditText
import com.example.criapp.abstractions.IModel
import com.example.criapp.models.requests.ResetPasswordRequest
import com.example.criapp.models.validators.ResetPasswordModelValidator

class ResetPasswordModel(
    val username: String,
    val editTextNewPassword: EditText,
    val editTextCode: EditText
) : IModel<ResetPasswordRequest> {
    override fun validate(): Boolean {
        return ResetPasswordModelValidator(this).isValid()
    }

    override fun toRequest(): String {
        return ResetPasswordRequest(
            username,
            editTextNewPassword.text.toString(),
            editTextCode.text.toString()
        ).toString()
    }

}