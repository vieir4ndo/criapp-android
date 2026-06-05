package com.example.criapp.data.repository
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.data.remote.toErrorMessage
import com.example.criapp.domain.repository.IAwardRepository
import com.example.criapp.models.responses.AwardResponse
import retrofit2.HttpException

class AwardRepositoryImpl(private val apiService: CriAppApiService) : IAwardRepository {

    override suspend fun getAwards(childId: String): Result<List<AwardResponse>> {
        return try {
            val response = apiService.getAwards(childId)
            if (response.success) {
                Result.success(response.data ?: emptyList())
            } else {
                Result.failure(
                    Exception(
                        response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)
                    )
                )
            }
        } catch (e: HttpException) {
            Result.failure(Exception(e.toErrorMessage()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}