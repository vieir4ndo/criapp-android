package com.example.criapp.model

import com.google.gson.Gson
import kotlinx.serialization.Serializable

@Serializable
class ResponseModel(
    val success: Boolean,
    val data: List<Any>?,
    val messages: List<Any>?) {

    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}