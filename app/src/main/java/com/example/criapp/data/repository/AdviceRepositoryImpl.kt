package com.example.criapp.data.repository

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.data.remote.toErrorMessage
import com.example.criapp.domain.repository.IAdviceRepository
import com.example.criapp.models.responses.AdviceResponse
import retrofit2.HttpException

class AdviceRepositoryImpl(private val apiService: CriAppApiService) : IAdviceRepository {

    override suspend fun getAdvices(): Result<List<AdviceResponse>> {
        return try {
            val response = apiService.getAdvices()
            if (response.success) {
                Result.success(response.data ?: emptyList())
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
