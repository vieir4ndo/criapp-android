package com.example.criapp.models.requests

import com.google.gson.Gson

class ResetPasswordRequest(
    val username : String,
    val newPassword : String,
    val code : String
) {
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}