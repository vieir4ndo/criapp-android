package com.example.criapp.domain.repository

import com.example.criapp.models.responses.NotificationHistoryResponse
import com.example.criapp.models.responses.NotificationResponse

interface INotificationRepository {
    suspend fun getPendingNotifications(): Result<List<NotificationResponse>>
    suspend fun getNotificationHistory(page: Int, pageSize: Int): Result<NotificationHistoryResponse>
    suspend fun markAsDelivered(id: String): Result<Unit>
    suspend fun markAsRead(id: String): Result<Unit>
}
