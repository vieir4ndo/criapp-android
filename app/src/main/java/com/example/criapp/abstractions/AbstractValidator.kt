package com.example.criapp.abstractions

import android.content.Context
import android.util.Patterns
import android.view.View
import android.widget.TextView
import com.example.criapp.R
import com.example.criapp.services.FeatureFlagsManager

abstract class AbstractValidator(var errorTextView: TextView, var context: Context) {

    private var rules: MutableList<Triple<Int, View, (View, Int) -> Boolean>> =
        emptyList<Triple<Int, View, (View, Int) -> Boolean>>().toMutableList()

    fun ruleFor(fieldNameRes: Int, editText: View, function: ((View, Int) -> Boolean)) {
        rules.add(Triple(fieldNameRes, editText, function));
    }

    fun isValid(): Boolean {
        errorTextView.text = ""
        var isValid = true;
        rules.forEach {
            val fieldNameRes = it.first
            val editText = it.second;
            val function = it.third;

            if (!function(editText, fieldNameRes)) {
                isValid = false;
            }
        }

        return isValid;
    }

    private fun addError(message: String) {
        val current = errorTextView.text.toString()
        errorTextView.text = if (current.isEmpty()) message else "$current\n$message"
    }

    fun isNotEmpty(input: View, inputNameRes: Int): Boolean {
        input as TextView
        if (input.text.toString().isEmpty() || input.text.toString().isBlank()) {
            addError(context.getString(R.string.validation_required, context.getString(inputNameRes)));
            return false;
        }
        return true;
    }

    fun isSpinnerNotEmpty(input: View, inputNameRes: Int): Boolean {
        input as com.example.criapp.CriAppDropdown
        if (input.selectedPosition == 0) {
            addError(context.getString(R.string.validation_required, context.getString(inputNameRes)))
            return false
        }
        return true
    }

    fun hasMaxLength100Char(input: View, inputNameRes: Int): Boolean {
        input as TextView
        if (input.text.toString().length > 100) {
            addError(context.getString(R.string.validation_max_100, context.getString(inputNameRes)));
            return false;
        }
        return true;
    }

    fun isValidUsername(input: View, inputNameRes: Int) : Boolean{
        input as TextView
        if (Patterns.EMAIL_ADDRESS.matcher(input.text.toString()).matches()) {
            return true;
        }

        if (FeatureFlagsManager.phoneNumberUsernameEnabled) {
            val pattern = Regex("^\\+?[1-9]\\d{7,14}$")
            if (pattern.matches(input.text.toString())) {
                return true
            }
        }

        addError(context.getString(R.string.validation_invalid, context.getString(inputNameRes)));

        return false;
    }


    fun isValidDate(input: View, inputNameRes: Int) : Boolean{
        input as TextView
        val pattern = Regex("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}")
        if (!pattern.matches(input.text.toString())) {
            addError(context.getString(R.string.validation_invalid_feminine, context.getString(inputNameRes)));
            return false;
        }

        return true;
    }
}