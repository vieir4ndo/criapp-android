package com.example.criapp.models

import com.google.gson.Gson

class ResetPasswordModel {
    var username = "";
    var newPassword = "";
    var code = "";

    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}