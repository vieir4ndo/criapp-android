package com.example.criapp.data.repository

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.domain.repository.IRecommendationRepository
import com.example.criapp.models.responses.RecommendationResponse

class RecommendationRepositoryImpl(private val apiService: CriAppApiService) : IRecommendationRepository {

    override suspend fun getRecommendations(): Result<List<RecommendationResponse>> {
        return try {
            val response = apiService.getRecommendations()
            if (response.success) {
                Result.success(response.data ?: emptyList())
            } else {
                Result.failure(
                    Exception(response.Messages?.joinToString("\n") ?: "Error desconocido")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
