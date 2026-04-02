package com.example.criapp.data.repository

import com.example.criapp.data.remote.CriAppHttpClient
import com.example.criapp.domain.repository.IResourceRepository
import com.example.criapp.models.responses.AudiovisualResourceResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResourceRepositoryImpl(private val httpClient: CriAppHttpClient) : IResourceRepository {

    private val gson = Gson()

    override suspend fun getResources(): Result<List<AudiovisualResourceResponse>> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.get("/audiovisual-resource") }
            if (response != null && response.success) {
                val json = gson.toJson(response.data)
                Result.success(gson.fromJson(json, Array<AudiovisualResourceResponse>::class.java).toList())
            } else {
                Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
