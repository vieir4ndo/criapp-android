package com.example.criapp.services

import android.util.Patterns
import android.widget.EditText

abstract class AbstractModel<T> {

    private var rules: MutableList<Triple<String, EditText, (EditText, String) -> Boolean>> =
        emptyList<Triple<String, EditText, (EditText, String) -> Boolean>>().toMutableList()

    fun ruleFor(fieldName: String, editText: EditText, function: ((EditText, String) -> Boolean)) {
        rules.add(Triple(fieldName, editText, function));
    }

    fun isValid(): Boolean {
        var isValid = true;
        rules.forEach {
            val fieldName = it.first
            val editText = it.second;
            val function = it.third;

            if (!function(editText, fieldName)) {
                isValid = false;
            }
        }

        return isValid;
    }

    fun isNotEmpty(input: EditText, inputName: String): Boolean {
        if (input.text.toString().isEmpty() || input.text.toString().isBlank()) {
            input.error = "$inputName es obligatorio";
            return false;
        }
        return true;
    }

    fun hasMaxLength100Char(input: EditText, inputName: String): Boolean {
        if (input.text.toString().length > 255) {
            input.error = "$inputName debe contener un máximo de 100 caracteres";
            return false;
        }
        return true;
    }

    fun isGreaterThan0(input: EditText, inputName: String): Boolean {
        if (input.text.toString().toInt() > 0) {
            input.error = "$inputName debe ser informado";
            return false;
        }
        return true;
    }

    fun isValidUsername(input: EditText, inputName: String) : Boolean{
        if (Patterns.EMAIL_ADDRESS.matcher(input.text.toString()).matches()) {
            return true;
        }

        val pattern = Regex("^(\\+34|34)?[6-9]\\d{8}\$")
        if (pattern.matches(input.text.toString())) {
            return true;
        }

        input.error = "$inputName es inválido";

        return false;
    }
}