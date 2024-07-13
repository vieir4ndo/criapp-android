package com.example.criapp.models.responses

import com.google.gson.Gson

class CommentResponse(
    val id : String,
    val name: String,
    val pcrName: String,
) {
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}