package com.example.criapp.model

import com.google.gson.Gson

class ResponseModel(
    val success: Boolean,
    val data: List<Any>?,
    val messages: List<String>?) {

    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}