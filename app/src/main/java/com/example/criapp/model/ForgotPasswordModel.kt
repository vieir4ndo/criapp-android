package com.example.criapp.model

import com.google.gson.Gson

class ForgotPasswordModel {
    var username = "";
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}