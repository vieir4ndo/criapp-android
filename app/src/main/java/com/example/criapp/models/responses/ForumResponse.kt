package com.example.criapp.models.responses

import com.google.gson.Gson

class ForumResponse(
    val id : String,
    val name: String,
    val description: String,
    val prcName: String,
    val prcId: String,
    val comments: Array<CommentResponse>
) {
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}