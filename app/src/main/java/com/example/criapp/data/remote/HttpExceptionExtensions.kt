package com.example.criapp.data.remote
import android.util.Log
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.HttpException
import java.io.IOException

fun HttpException.toErrorMessage(fallback: String = CRIAppApplication.context.getString(R.string.error_unknown)): String {
    if (code() in 500..599) {
        return CRIAppApplication.context.getString(R.string.error_server)
    }
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

suspend fun <T> safeApiCall(tag: String, block: suspend () -> Result<T>): Result<T> {
    return try {
        block()
    } catch (e: HttpException) {
        Result.failure(Exception(e.toErrorMessage()))
    } catch (e: IOException) {
        Result.failure(Exception(CRIAppApplication.context.getString(R.string.error_no_internet)))
    } catch (e: Exception) {
        Log.e("CRIApp", "Unexpected error in $tag", e)
        Result.failure(Exception(CRIAppApplication.context.getString(R.string.error_unknown)))
    }
}
