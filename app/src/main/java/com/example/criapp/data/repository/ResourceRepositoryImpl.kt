package com.example.criapp.data.repository

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.domain.repository.IResourceRepository
import com.example.criapp.models.responses.AudiovisualResourceResponse

class ResourceRepositoryImpl(private val apiService: CriAppApiService) : IResourceRepository {

    override suspend fun getResources(): Result<List<AudiovisualResourceResponse>> {
        return try {
            val response = apiService.getResources()
            if (response.success) {
                Result.success(response.data ?: emptyList())
            } else {
                Result.failure(Exception(response.Messages?.joinToString("\n") ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
