package com.example.criapp.data.repository

import com.example.criapp.data.remote.CriAppHttpClient
import com.example.criapp.domain.repository.IAuthRepository
import com.example.criapp.models.requests.LoginUserRequest
import com.example.criapp.models.responses.LoginUserResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(private val httpClient: CriAppHttpClient) : IAuthRepository {

    private val gson = Gson()

    override suspend fun login(username: String, password: String): Result<LoginUserResponse> {
        return try {
            val body = LoginUserRequest(username, password).toString()
            val response = withContext(Dispatchers.IO) { httpClient.post("/user/login", body) }
            if (response != null && response.success) {
                val json = gson.toJson(response.data.first())
                Result.success(gson.fromJson(json, LoginUserResponse::class.java))
            } else {
                Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createUser(requestBody: String): Result<Unit> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.post("/user", requestBody) }
            if (response != null && response.success) Result.success(Unit)
            else Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun forgotPassword(requestBody: String): Result<Unit> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.post("/user/forgot-password", requestBody) }
            if (response != null && response.success) Result.success(Unit)
            else Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(requestBody: String): Result<Unit> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.post("/user/reset-password", requestBody) }
            if (response != null && response.success) Result.success(Unit)
            else Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
