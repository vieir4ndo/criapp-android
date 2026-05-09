package com.example.criapp.abstractions

import android.content.Context
import android.widget.TextView

interface IModel<T> {
    fun validate(errorTextView: TextView, context: Context) : Boolean;
    fun toRequest(): String;
}