package com.example.criapp.domain.repository

import com.example.criapp.models.responses.AdviceResponse

interface IAdviceRepository {
    suspend fun getAdvices(): Result<List<AdviceResponse>>
}
