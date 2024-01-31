package com.example.criapp.model

import android.widget.EditText
import com.example.criapp.services.AbstractModel
import com.google.gson.Gson

class CreateUserModel(
  editTextName: EditText,
  editTextBirthDate: EditText,
  editTextUsername: EditText,
  editTextPassword: EditText
) : AbstractModel<CreateUserModel>() {
    var name = "";
    var birthDate = "";
    var sex = 0;
    var username = "";
    var password = "";
    var familyType = 0;
    var educationalLevel = 0;
    var numberOfChildren = 0;

    init {
        ruleFor("Nombre", editTextName, this::isNotEmpty)
        ruleFor("Nombre", editTextName, this::hasMaxLength100Char)

        ruleFor("Nombre de usuario", editTextUsername, this::isNotEmpty)
        ruleFor("Nombre de usuario", editTextUsername, this::isValidUsername)

        ruleFor("Fecha de nacimiento", editTextBirthDate, this::isNotEmpty)

        ruleFor("Contraseña", editTextPassword, this::isNotEmpty)
    }

    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this)
    }
}