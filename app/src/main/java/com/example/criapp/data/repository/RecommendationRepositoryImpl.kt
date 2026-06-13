package com.example.criapp.data.repository
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.data.remote.toErrorMessage
import com.example.criapp.data.remote.safeApiCall
import com.example.criapp.domain.repository.IRecommendationRepository
import com.example.criapp.models.requests.CreateCheckpointRequest
import com.example.criapp.models.responses.RecommendationResponse

class RecommendationRepositoryImpl(private val apiService: CriAppApiService) : IRecommendationRepository {

    override suspend fun getRecommendations(childId: String): Result<List<RecommendationResponse>> {
        return safeApiCall("RecommendationRepositoryImpl") {
            val response = apiService.getRecommendations(childId)
            if (response.success) {
                Result.success(response.data ?: emptyList())
            } else {
                Result.failure(
                    Exception(response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown))
                )
            }
        }
    }

    override suspend fun registerCheckpoint(childId: String, recommendationId: Int, completed: Boolean): Result<Boolean> {
        return safeApiCall("RecommendationRepositoryImpl") {
            val response = apiService.registerCheckpoint(childId, recommendationId, CreateCheckpointRequest(completed))
            if (response.success) {
                val assimilated = response.data?.firstOrNull()?.assimilated ?: false
                Result.success(assimilated)
            } else {
                Result.failure(
                    Exception(response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown))
                )
            }
        }
    }
}
