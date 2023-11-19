package com.example.criapp.model

import com.google.gson.Gson

class LoginUserModel {

    var username = "";
    var password = "";

    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}