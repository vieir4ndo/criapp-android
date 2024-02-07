package com.example.criapp.models.requests

import com.google.gson.Gson

class ForgotPasswordRequest(
    val username : String
) {
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}