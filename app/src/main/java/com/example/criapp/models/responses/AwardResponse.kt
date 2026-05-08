package com.example.criapp.models.responses

import com.google.gson.Gson

class AwardResponse(
    val id : String,
    val type: Int
) {
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}