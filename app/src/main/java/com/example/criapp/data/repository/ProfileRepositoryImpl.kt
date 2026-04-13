package com.example.criapp.data.repository

import com.example.criapp.data.remote.CriAppHttpClient
import com.example.criapp.domain.repository.IProfileRepository
import com.example.criapp.models.responses.UserResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileRepositoryImpl(private val httpClient: CriAppHttpClient) : IProfileRepository {

    private val gson = Gson()

    override suspend fun getProfile(): Result<UserResponse> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.getAuthenticated("/user") }
            if (response != null && response.success) {
                val json = gson.toJson(response.data.first())
                Result.success(gson.fromJson(json, UserResponse::class.java))
            } else {
                Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(requestBody: String): Result<Unit> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.patchAuthenticated("/user", requestBody) }
            if (response != null && response.success) Result.success(Unit)
            else Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
