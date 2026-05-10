package com.example.criapp.domain.repository

import com.example.criapp.models.responses.NotificationResponse

interface INotificationRepository {
    suspend fun getPendingNotifications(): Result<List<NotificationResponse>>
    suspend fun getNotificationHistory(): Result<List<NotificationResponse>>
    suspend fun markAsDelivered(id: String): Result<Unit>
    suspend fun markAsRead(id: String): Result<Unit>
}
