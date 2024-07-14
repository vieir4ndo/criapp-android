package com.example.criapp.models.responses

import com.google.gson.Gson

class CommentResponse(
    val id : String,
    val text: String,
    val prcName: String,
) {
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}