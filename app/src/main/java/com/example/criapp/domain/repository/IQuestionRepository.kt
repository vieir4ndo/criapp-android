package com.example.criapp.domain.repository

import com.example.criapp.models.responses.QuestionResponse

interface IQuestionRepository {
    suspend fun getQuestions(childId: String): Result<List<QuestionResponse>>
    suspend fun answerQuestion(requestBody: String): Result<Unit>
}
