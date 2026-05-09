package com.example.criapp.data.remote

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.HttpException

fun HttpException.toErrorMessage(fallback: String = "Error desconocido"): String {
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
