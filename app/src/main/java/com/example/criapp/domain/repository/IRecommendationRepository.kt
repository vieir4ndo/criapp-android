package com.example.criapp.domain.repository

import com.example.criapp.models.responses.RecommendationResponse

interface IRecommendationRepository {
    suspend fun getRecommendationsByChildBookId(childBookId: String): Result<List<RecommendationResponse>>
    suspend fun registerCheckpoint(childBookId: String, childBookRecommendationId: String, completed: Boolean): Result<Boolean>
}
