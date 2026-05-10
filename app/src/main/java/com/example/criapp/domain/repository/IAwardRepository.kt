package com.example.criapp.domain.repository

import com.example.criapp.models.responses.AwardResponse

interface IAwardRepository {
    suspend fun getAwards(childId: String): Result<List<AwardResponse>>
}