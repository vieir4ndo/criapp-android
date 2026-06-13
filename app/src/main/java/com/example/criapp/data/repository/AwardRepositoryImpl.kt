package com.example.criapp.data.repository
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.data.remote.safeApiCall
import com.example.criapp.domain.repository.IAwardRepository
import com.example.criapp.models.responses.AwardResponse

class AwardRepositoryImpl(private val apiService: CriAppApiService) : IAwardRepository {

    override suspend fun getAwardsByChildBookId(childBookId: String): Result<List<AwardResponse>> {
        return safeApiCall("AwardRepositoryImpl") {
            val response = apiService.getAwardsByChildBookId(childBookId)
            if (response.success) {
                Result.success(response.data ?: emptyList())
            } else {
                Result.failure(
                    Exception(
                        response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)
                    )
                )
            }
        }
    }
}