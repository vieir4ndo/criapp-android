package com.example.criapp.data.repository
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.domain.repository.IQuestionRepository
import com.example.criapp.models.responses.QuestionResponse
import com.example.criapp.data.remote.toErrorMessage
import com.example.criapp.data.remote.safeApiCall
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class QuestionRepositoryImpl(private val apiService: CriAppApiService) : IQuestionRepository {

    override suspend fun getQuestions(childId: String): Result<List<QuestionResponse>> {
        return safeApiCall("QuestionRepositoryImpl") {
            val response = apiService.getQuestions(childId)
            if (response.success) {
                Result.success(response.data ?: emptyList())
            } else {
                Result.failure(Exception(response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)))
            }
        }
    }

    override suspend fun answerQuestion(requestBody: String): Result<Unit> {
        return safeApiCall("QuestionRepositoryImpl") {
            val body = requestBody.toRequestBody("application/json".toMediaType())
            val response = apiService.answerQuestion(body)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)))
        }
    }
}
