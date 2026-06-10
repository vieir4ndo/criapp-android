package com.example.criapp.data.repository
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.data.remote.toErrorMessage
import com.example.criapp.data.remote.safeApiCall
import com.example.criapp.domain.repository.INotificationRepository
import com.example.criapp.models.responses.NotificationHistoryResponse
import com.example.criapp.models.responses.NotificationResponse

class NotificationRepositoryImpl(private val apiService: CriAppApiService) : INotificationRepository {

    override suspend fun getPendingNotifications(): Result<List<NotificationResponse>> {
        return safeApiCall("NotificationRepositoryImpl") {
            val response = apiService.getPendingNotifications()
            if (response.success) {
                Result.success(response.data ?: emptyList())
            } else {
                Result.failure(Exception(response.Messages?.joinToString("\n") ?: "Unknown error"))
            }
        }
    }

    override suspend fun getNotificationHistory(page: Int, pageSize: Int): Result<NotificationHistoryResponse> {
        return safeApiCall("NotificationRepositoryImpl") {
            val response = apiService.getNotificationHistory(page, pageSize)
            if (response.success) {
                val history = response.data?.firstOrNull()
                    ?: return@safeApiCall Result.failure(Exception("Unknown error"))
                Result.success(history)
            } else {
                Result.failure(Exception(response.Messages?.joinToString("\n") ?: "Unknown error"))
            }
        }
    }

    override suspend fun markAsDelivered(id: String): Result<Unit> {
        return safeApiCall("NotificationRepositoryImpl") {
            val response = apiService.markNotificationDelivered(id)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.Messages?.joinToString("\n") ?: "Unknown error"))
        }
    }

    override suspend fun markAsRead(id: String): Result<Unit> {
        return safeApiCall("NotificationRepositoryImpl") {
            val response = apiService.markNotificationRead(id)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.Messages?.joinToString("\n") ?: "Unknown error"))
        }
    }
}
