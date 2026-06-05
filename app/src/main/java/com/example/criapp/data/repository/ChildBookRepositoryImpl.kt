package com.example.criapp.data.repository
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.data.remote.toErrorMessage
import com.example.criapp.domain.repository.IChildBookRepository
import com.example.criapp.models.responses.ChildBookSummaryResponse
import retrofit2.HttpException

class ChildBookRepositoryImpl(private val apiService: CriAppApiService) : IChildBookRepository {

    override suspend fun getChildBookScore(childId: String): Result<ChildBookSummaryResponse?> {
        return try {
            val response = apiService.getChildBookScore(childId)
            if (response.success) {
                Result.success(response.data?.firstOrNull())
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
