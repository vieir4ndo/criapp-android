package com.example.criapp.domain.repository

import com.example.criapp.models.responses.AwardResponse

interface IAwardRepository {
    suspend fun getAwards(): Result<List<AwardResponse>>

}