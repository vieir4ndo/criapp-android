package com.example.criapp.models.responses

import com.google.gson.Gson

class AwardResponse(
    val id: String,
    val type: Int,
    val text: String,
    val description: String?,
    val image: String?
) {
    override fun toString(): String {
        val gson = Gson();
        return gson.toJson(this);
    }
}