package com.example.criapp.abstractions

import android.widget.TextView

interface IModel<T> {
    fun validate(errorTextView: TextView) : Boolean;
    fun toRequest(): String;
}