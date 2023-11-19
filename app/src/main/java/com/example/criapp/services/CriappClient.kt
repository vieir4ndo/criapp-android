package com.example.criapp.services

import com.example.criapp.model.ResponseModel
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class CriappClient {

    val baseUri = "https://criapp-api-c5e0110cc8ae.herokuapp.com"
    val gson = Gson()
    fun postWithoutCredentials(path: String, body : String, headers: List<String> = listOf("application/json", "charset=utf-8")) : ResponseModel? {

        val headersHttp = MediaType.parse(headers.joinToString(";"));

        val requestBody = RequestBody.create(headersHttp, body);

        val client = OkHttpClient();

        val request = Request.Builder().url(baseUri + path).post(requestBody).build();

        val response = client.newCall(request).execute();

        return gson.fromJson(response.body().toString(), ResponseModel::class.java)
    }
}