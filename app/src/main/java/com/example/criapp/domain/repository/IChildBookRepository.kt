package com.example.criapp.domain.repository

import com.example.criapp.models.responses.ChildBookSummaryResponse

interface IChildBookRepository {
    suspend fun getChildBookScore(childBookId: String): Result<ChildBookSummaryResponse?>
}
