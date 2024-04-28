package com.example.criapp.models.responses

import com.google.gson.Gson

class AudiovisualResourceResponse(
    val id : String,
    val name: String,
    val description: String,
    val iframe: String,
) {
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}