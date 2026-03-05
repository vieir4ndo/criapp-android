package com.example.criapp.services

import android.content.SharedPreferences
import com.example.criapp.models.responses.ResponseModel
import com.google.gson.Gson
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class CriAppClient(private val sharedPreferences: SharedPreferences) {
    private val gson = Gson()
    // TODO: Should put these variables as env vars
    private val baseUri = "http://10.0.2.2:4500"
    private val shouldLog : Boolean = true;

    fun postAuthenticated(path: String, body : String) : ResponseModel? {
        val headers = mapOf(
            "Authorization" to getToken(),
            "Content-Type" to "application/json",
            "charset" to "utf-8"
        );

        return post(path, body, headers);
    }

    fun patchAuthenticated(path: String, body : String) : ResponseModel? {
        val headers = mapOf(
            "Authorization" to getToken(),
            "Content-Type" to "application/json",
            "charset" to "utf-8"
        );

        return patch(path, body, headers);
    }

    fun getAuthenticated(path: String) : ResponseModel?{
        val headers = mapOf(
            "Authorization" to getToken(),
            "Content-Type" to "application/json",
            "charset" to "utf-8"
        );

        return get(path, headers)
    }

    fun deleteAuthenticated(path: String) : ResponseModel?{
        val headers = mapOf(
            "Authorization" to getToken(),
            "Content-Type" to "application/json",
            "charset" to "utf-8"
        );

        return delete(path, headers)
    }

    fun post(path: String, body : String, headers: Map<String, String> = mapOf(
        "Content-Type" to "application/json",
        "charset" to "utf-8"
    )) : ResponseModel?
    {

        val uri = baseUri + path;

        if (shouldLog){
            println("Url: '$uri'");
            println("Body: '$body'");
            println("Headers: '$headers'");
        }

        val headersHttp = MediaType.parse(listOf("application/json", "charset=utf-8").joinToString(";"));

        val okHttpHeaders = Headers.of(headers)

        val requestBody = RequestBody.create(headersHttp, body);

        val client = OkHttpClient();

        val request = Request.Builder().url(uri).headers(okHttpHeaders).post(requestBody).build();

        val response = client.newCall(request).execute();

        val responseBody = response.body()?.string();

        if (shouldLog){
            println("Status Code: '${response.code()}'");
            println("Body: '$responseBody'");
        }

        return gson.fromJson(responseBody, ResponseModel::class.java)
    }

    fun patch(path: String, body : String, headers: Map<String, String> = mapOf(
        "Content-Type" to "application/json",
        "charset" to "utf-8"
    )) : ResponseModel?
    {

        val uri = baseUri + path;

        if (shouldLog){
            println("Url: '$uri'");
            println("Body: '$body'");
            println("Headers: '$headers'");
        }

        val headersHttp = MediaType.parse(listOf("application/json", "charset=utf-8").joinToString(";"));

        val okHttpHeaders = Headers.of(headers)

        val requestBody = RequestBody.create(headersHttp, body);

        val client = OkHttpClient();

        val request = Request.Builder().url(uri).headers(okHttpHeaders).patch(requestBody).build();

        val response = client.newCall(request).execute();

        val responseBody = response.body()?.string();

        if (shouldLog){
            println("Status Code: '${response.code()}'");
            println("Body: '$responseBody'");
        }

        return gson.fromJson(responseBody, ResponseModel::class.java)
    }

    fun get(path: String, headers: Map<String, String> = mapOf(
        "Content-Type" to "application/json",
        "charset" to "utf-8"
    )) : ResponseModel?
    {
        val uri = baseUri + path;

        if (shouldLog) {
            println("Url: '$uri'");
            println("Headers: '$headers'");
        }

        val okHttpHeaders = Headers.of(headers)

        val client = OkHttpClient();

        val request = Request.Builder().url(uri).headers(okHttpHeaders).get().build();

        val response = client.newCall(request).execute();

        val responseBody = response.body()?.string();

        if (shouldLog) {
            println("Status Code: '${response.code()}'");
            println("Body: '$responseBody'");
        }

        return gson.fromJson(responseBody, ResponseModel::class.java)
    }

    private fun getToken() : String
    {
        val token = sharedPreferences.getString("token", "")
        //TODO: validate expiresIn and refresh token if necessary
        val expiresIn = sharedPreferences.getString("token_expires_in", "")

        return "Bearer $token";
    }

    fun delete(path: String, headers: Map<String, String> = mapOf(
        "Content-Type" to "application/json",
        "charset" to "utf-8"
    )) : ResponseModel?
    {
        val uri = baseUri + path;

        if (shouldLog) {
            println("Url: '$uri'");
            println("Headers: '$headers'");
        }

        val okHttpHeaders = Headers.of(headers)

        val client = OkHttpClient();

        val request = Request.Builder().url(uri).headers(okHttpHeaders).delete().build();

        val response = client.newCall(request).execute();

        val responseBody = response.body()?.string();

        if (shouldLog) {
            println("Status Code: '${response.code()}'");
            println("Body: '$responseBody'");
        }

        return gson.fromJson(responseBody, ResponseModel::class.java)
    }
}