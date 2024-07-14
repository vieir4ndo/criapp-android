package com.example.criapp.models.requests

import com.google.gson.Gson

class CreateForumRequest(
    private val name:String,
    private val description:String
) {
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this)
    }
}