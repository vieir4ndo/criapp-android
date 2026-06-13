package com.example.criapp.domain.repository

import com.example.criapp.models.responses.RecommendationResponse

interface IRecommendationRepository {
    suspend fun getRecommendations(childId: String): Result<List<RecommendationResponse>>
    suspend fun registerCheckpoint(childId: String, recommendationId: Int, completed: Boolean): Result<Boolean>
}
