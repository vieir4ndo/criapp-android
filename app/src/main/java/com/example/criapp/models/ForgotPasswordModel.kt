package com.example.criapp.models

import com.google.gson.Gson

class ForgotPasswordModel {
    var username = "";
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}