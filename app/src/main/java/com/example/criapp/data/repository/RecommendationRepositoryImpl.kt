package com.example.criapp.data.repository

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.data.remote.toErrorMessage
import com.example.criapp.domain.repository.IRecommendationRepository
import com.example.criapp.models.responses.RecommendationResponse
import retrofit2.HttpException

class RecommendationRepositoryImpl(private val apiService: CriAppApiService) : IRecommendationRepository {

    override suspend fun getRecommendations(childId: String): Result<List<RecommendationResponse>> {
        return try {
            val response = apiService.getChildBook(childId)
            if (response.success) {
                Result.success(response.data?.firstOrNull()?.recommendations ?: emptyList())
            } else {
                Result.failure(
                    Exception(response.Messages?.joinToString("\n") ?: "Error desconocido")
                )
            }
        } catch (e: HttpException) {
            Result.failure(Exception(e.toErrorMessage()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
