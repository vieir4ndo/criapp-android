package com.example.criapp.models.requests

import com.google.gson.Gson

class LoginUserRequest(
    val username: String,
    val password: String
) {
    override fun toString(): String {
        val gson = Gson();
        return gson.toJson(this);
    }
}