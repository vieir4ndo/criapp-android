package com.example.criapp.models

import com.google.gson.Gson

class QuestionModel(
    val id : String,
    val text: String,
    val active: Boolean,
    val adminId: String,
    val type: Int
) {
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}