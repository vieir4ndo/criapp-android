package com.example.criapp.services

import com.example.criapp.model.ResponseModel
import com.google.gson.Gson
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class CriappClient {

    private val baseUri = "https://criapp-api-c5e0110cc8ae.herokuapp.com"
    private val gson = Gson()
    fun post(path: String, body : String, headers: Map<String, String> = mapOf(
        "Content-Type" to "application/json",
        "charset" to "utf-8"
    )) : ResponseModel? {

        val uri = baseUri + path;

        println("Url: '$uri'");
        println("Body: '$body'");
        println("Headers: '$headers'");

        val headersHttp = MediaType.parse(listOf("application/json", "charset=utf-8").joinToString(";"));

        val okHttpHeaders = Headers.of(headers)

        val requestBody = RequestBody.create(headersHttp, body);

        val client = OkHttpClient();

        val request = Request.Builder().url(uri).headers(okHttpHeaders).post(requestBody).build();

        val response = client.newCall(request).execute();

        val responseBody = response.body()?.string();

        println("Status Code: '${response.code()}'");
        println("Body: '$responseBody'");

        return gson.fromJson(responseBody, ResponseModel::class.java)
    }
}