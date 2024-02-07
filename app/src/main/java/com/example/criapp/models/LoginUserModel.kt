package com.example.criapp.models
import android.widget.EditText
import com.example.criapp.abstractions.IModel
import com.example.criapp.models.requests.LoginUserRequest
import com.example.criapp.models.validators.LoginUserModelValidator

class LoginUserModel(
    val editTextUsername: EditText,
    val editTextPassword: EditText
) : IModel<LoginUserModel>{
    override fun validate(): Boolean {
        return LoginUserModelValidator(this).isValid();
    }

    override fun toRequest(): String {
        return LoginUserRequest(
            editTextUsername.text.toString(),
            editTextPassword.text.toString()
        ).toString();
    }
}