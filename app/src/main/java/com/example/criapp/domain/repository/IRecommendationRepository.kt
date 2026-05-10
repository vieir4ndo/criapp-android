package com.example.criapp.domain.repository

import com.example.criapp.models.responses.RecommendationResponse

interface IRecommendationRepository {
    suspend fun getRecommendations(childId: String): Result<List<RecommendationResponse>>
}
