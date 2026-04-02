package com.example.criapp.data.repository

import com.example.criapp.data.remote.CriAppHttpClient
import com.example.criapp.domain.repository.IChildRepository
import com.example.criapp.models.responses.ChildResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChildRepositoryImpl(private val httpClient: CriAppHttpClient) : IChildRepository {

    private val gson = Gson()

    override suspend fun getChildren(): Result<List<ChildResponse>> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.getAuthenticated("/child") }
            if (response != null && response.success) {
                val json = gson.toJson(response.data.first())
                Result.success(gson.fromJson(json, Array<ChildResponse>::class.java).toList())
            } else {
                Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createChild(requestBody: String): Result<Unit> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.postAuthenticated("/child", requestBody) }
            if (response != null && response.success) Result.success(Unit)
            else Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateChild(childId: String, requestBody: String): Result<Unit> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.patchAuthenticated("/child/$childId", requestBody) }
            if (response != null && response.success) Result.success(Unit)
            else Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
