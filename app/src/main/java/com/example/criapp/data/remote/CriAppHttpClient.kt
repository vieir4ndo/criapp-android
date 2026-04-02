package com.example.criapp.data.remote

import com.example.criapp.BuildConfig
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.models.responses.ResponseModel
import com.google.gson.Gson
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class CriAppHttpClient(private val prefsManager: PreferencesManager) {

    private val gson = Gson()
    private val baseUri = BuildConfig.BASE_URL
    private val client = OkHttpClient()

    fun postAuthenticated(path: String, body: String): ResponseModel? {
        return post(path, body, authenticatedHeaders())
    }

    fun patchAuthenticated(path: String, body: String): ResponseModel? {
        return patch(path, body, authenticatedHeaders())
    }

    fun getAuthenticated(path: String): ResponseModel? {
        return get(path, authenticatedHeaders())
    }

    fun deleteAuthenticated(path: String): ResponseModel? {
        return delete(path, authenticatedHeaders())
    }

    fun post(
        path: String,
        body: String,
        headers: Map<String, String> = jsonHeaders()
    ): ResponseModel? {
        val mediaType = MediaType.parse("application/json;charset=utf-8")
        val requestBody = RequestBody.create(mediaType, body)
        val request = Request.Builder()
            .url(baseUri + path)
            .headers(Headers.of(headers))
            .post(requestBody)
            .build()
        return executeAndParse(request)
    }

    fun patch(
        path: String,
        body: String,
        headers: Map<String, String> = jsonHeaders()
    ): ResponseModel? {
        val mediaType = MediaType.parse("application/json;charset=utf-8")
        val requestBody = RequestBody.create(mediaType, body)
        val request = Request.Builder()
            .url(baseUri + path)
            .headers(Headers.of(headers))
            .patch(requestBody)
            .build()
        return executeAndParse(request)
    }

    fun get(
        path: String,
        headers: Map<String, String> = jsonHeaders()
    ): ResponseModel? {
        val request = Request.Builder()
            .url(baseUri + path)
            .headers(Headers.of(headers))
            .get()
            .build()
        return executeAndParse(request)
    }

    fun delete(
        path: String,
        headers: Map<String, String> = jsonHeaders()
    ): ResponseModel? {
        val request = Request.Builder()
            .url(baseUri + path)
            .headers(Headers.of(headers))
            .delete()
            .build()
        return executeAndParse(request)
    }

    private fun executeAndParse(request: Request): ResponseModel? {
        val response = client.newCall(request).execute()
        val responseBody = response.body()?.string()
        return gson.fromJson(responseBody, ResponseModel::class.java)
    }

    private fun authenticatedHeaders(): Map<String, String> = mapOf(
        "Authorization" to prefsManager.getAuthorizationHeader(),
        "Content-Type" to "application/json",
        "charset" to "utf-8"
    )

    private fun jsonHeaders(): Map<String, String> = mapOf(
        "Content-Type" to "application/json",
        "charset" to "utf-8"
    )
}
