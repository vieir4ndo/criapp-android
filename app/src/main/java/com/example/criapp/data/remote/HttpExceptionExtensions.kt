package com.example.criapp.data.remote
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.HttpException

fun HttpException.toErrorMessage(fallback: String = CRIAppApplication.context.getString(R.string.error_unknown)): String {
    return try {
        val body = response()?.errorBody()?.string()?.takeIf { it.isNotBlank() } ?: return fallback
        @Suppress("DEPRECATION")
        val json: JsonObject = JsonParser().parse(body).asJsonObject
        val messages = json.getAsJsonArray("Messages")
        messages?.joinToString("\n") { it.asString }?.takeIf { it.isNotBlank() } ?: fallback
    } catch (_: Exception) {
        fallback
    }
}
