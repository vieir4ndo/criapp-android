package com.example.criapp.data.repository
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.data.remote.toErrorMessage
import com.example.criapp.data.remote.safeApiCall
import com.example.criapp.domain.repository.IChildBookRepository
import com.example.criapp.models.responses.ChildBookSummaryResponse

class ChildBookRepositoryImpl(private val apiService: CriAppApiService) : IChildBookRepository {

    override suspend fun getChildBookScore(childBookId: String): Result<ChildBookSummaryResponse?> {
        return safeApiCall("ChildBookRepositoryImpl") {
            val response = apiService.getChildBookScore(childBookId)
            if (response.success) {
                Result.success(response.data?.firstOrNull())
            } else {
                Result.failure(Exception(response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)))
            }
        }
    }
}
