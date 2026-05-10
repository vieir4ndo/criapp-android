package com.example.criapp.models.responses

import com.google.gson.Gson

class QuestionResponse(
    val id : String,
    val text: String,
    val active: Boolean,
    val type: Int,
    val scoringType: Int
) {
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}