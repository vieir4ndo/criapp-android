package com.example.criapp.data.repository

import com.example.criapp.data.remote.CriAppHttpClient
import com.example.criapp.domain.repository.IQuestionRepository
import com.example.criapp.models.responses.QuestionResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuestionRepositoryImpl(private val httpClient: CriAppHttpClient) : IQuestionRepository {

    private val gson = Gson()

    override suspend fun getQuestions(childId: String): Result<List<QuestionResponse>> {
        return try {
            val response = withContext(Dispatchers.IO) {
                httpClient.getAuthenticated("/question?childId=$childId")
            }
            if (response != null && response.success) {
                val json = gson.toJson(response.data)
                Result.success(gson.fromJson(json, Array<QuestionResponse>::class.java).toList())
            } else {
                Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun answerQuestion(requestBody: String): Result<Unit> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.postAuthenticated("/answer", requestBody) }
            if (response != null && response.success) Result.success(Unit)
            else Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
