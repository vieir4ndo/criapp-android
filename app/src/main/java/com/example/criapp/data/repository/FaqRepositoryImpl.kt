package com.example.criapp.data.repository

import com.example.criapp.data.remote.CriAppHttpClient
import com.example.criapp.domain.repository.IFaqRepository
import com.example.criapp.models.responses.FaqResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FaqRepositoryImpl(private val httpClient: CriAppHttpClient) : IFaqRepository {

    private val gson = Gson()

    override suspend fun getFaqs(): Result<List<FaqResponse>> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.get("/faq") }
            if (response != null && response.success) {
                val json = gson.toJson(response.data)
                Result.success(gson.fromJson(json, Array<FaqResponse>::class.java).toList())
            } else {
                Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
