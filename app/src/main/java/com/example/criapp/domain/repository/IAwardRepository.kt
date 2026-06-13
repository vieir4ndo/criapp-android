package com.example.criapp.domain.repository

import com.example.criapp.models.responses.AwardResponse

interface IAwardRepository {
    suspend fun getAwardsByChildBookId(childBookId: String): Result<List<AwardResponse>>
}