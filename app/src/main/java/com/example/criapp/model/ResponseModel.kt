package com.example.criapp.model

import com.google.gson.Gson
import kotlinx.serialization.Serializable

@Serializable
class ResponseModel(
    var Success: Boolean,
    var Data: List<Any>,
    var Messages: List<String>) {

    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}