package com.example.criapp.domain.repository

import com.example.criapp.models.responses.FaqResponse

interface IFaqRepository {
    suspend fun getFaqs(): Result<List<FaqResponse>>
}
