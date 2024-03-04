package com.example.criapp.abstractions

import android.util.Patterns
import android.view.View
import android.widget.Spinner
import android.widget.TextView

abstract class AbstractValidator(var errorTextView: TextView) {

    private var rules: MutableList<Triple<String, View, (View, String) -> Boolean>> =
        emptyList<Triple<String, View, (View, String) -> Boolean>>().toMutableList()

    fun ruleFor(fieldName: String, editText: View, function: ((View, String) -> Boolean)) {
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

    fun isNotEmpty(input: View, inputName: String): Boolean {
        input as TextView
        if (input.text.toString().isEmpty() || input.text.toString().isBlank()) {
            errorTextView.text = errorTextView.text.toString().plus("\n$inputName es obligatorio");
            return false;
        }
        return true;
    }

    fun isSpinnerNotEmpty(input: View, inputName: String): Boolean {
        input as Spinner
        val adapter = input.getAdapter() as CriAppSpinnerAdapter;
        if (adapter.position == 0) {
            errorTextView.text = errorTextView.text.toString().plus("\n$inputName es obligatorio");
            return false;
        }
        return true;
    }

    fun hasMaxLength100Char(input: View, inputName: String): Boolean {
        input as TextView
        if (input.text.toString().length > 100) {
            errorTextView.text = errorTextView.text.toString().plus("\n$inputName debe contener un máximo de 100 caracteres");
            return false;
        }
        return true;
    }

    fun isValidUsername(input: View, inputName: String) : Boolean{
        input as TextView
        if (Patterns.EMAIL_ADDRESS.matcher(input.text.toString()).matches()) {
            return true;
        }

        val pattern = Regex("^(\\+34|34)?[6-9]\\d{8}\$")
        if (pattern.matches(input.text.toString())) {
            return true;
        }

        errorTextView.text = errorTextView.text.toString().plus("\n$inputName es inválido");

        return false;
    }


    fun isValidDate(input: View, inputName: String) : Boolean{
        input as TextView
        val pattern = Regex("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}")
        if (!pattern.matches(input.text.toString())) {
            errorTextView.text = errorTextView.text.toString().plus("\n$inputName es inválida");
            return false;
        }

        return true;
    }
}