package com.example.criapp.data.repository
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.data.remote.toErrorMessage
import com.example.criapp.domain.repository.IFaqRepository
import com.example.criapp.models.responses.FaqResponse
import retrofit2.HttpException

class FaqRepositoryImpl(private val apiService: CriAppApiService) : IFaqRepository {

    override suspend fun getFaqs(): Result<List<FaqResponse>> {
        return try {
            val response = apiService.getFaqs()
            if (response.success) {
                Result.success(response.data ?: emptyList())
            } else {
                Result.failure(Exception(response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(e.toErrorMessage()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
