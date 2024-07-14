package com.example.criapp.models.requests

import com.google.gson.Gson

class CreateCommentRequest(
    val forumId : String,
    val text : String
) {
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this)
    }
}