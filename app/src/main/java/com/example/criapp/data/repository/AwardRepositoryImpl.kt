package com.example.criapp.data.repository

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.data.remote.toErrorMessage
import com.example.criapp.domain.repository.IAwardRepository
import com.example.criapp.models.responses.AwardResponse
import retrofit2.HttpException

class AwardRepositoryImpl(private val apiService: CriAppApiService) : IAwardRepository {

    override suspend fun getAwards(): Result<List<AwardResponse>> {
        return try {
            val response = apiService.getAwards()
            if (response.success) {
                Result.success(response.data ?: emptyList())
            } else {
                Result.failure(
                    Exception(
                        response.Messages?.joinToString("\n") ?: "Error desconocido"
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