package com.example.criapp.models.validators

import com.example.criapp.abstractions.AbstractValidator
import com.example.criapp.models.LoginUserModel

class LoginUserModelValidator(loginUserModel: LoginUserModel) : AbstractValidator() {

    init {
        ruleFor("Nombre de usuario", loginUserModel.editTextUsername, this::isNotEmpty)
        ruleFor("Nombre de usuario", loginUserModel.editTextUsername, this::isValidUsername)

        ruleFor("Contraseña", loginUserModel.editTextPassword, this::isNotEmpty)
    }
}