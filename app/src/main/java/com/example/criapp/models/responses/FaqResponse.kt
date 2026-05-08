package com.example.criapp.models.responses

import com.google.gson.Gson

class FaqResponse(
    val id : String,
    val question: String,
    val answer: String
) {
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}