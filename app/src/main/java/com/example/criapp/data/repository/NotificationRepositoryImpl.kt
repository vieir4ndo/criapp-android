package com.example.criapp.data.repository

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.data.remote.toErrorMessage
import com.example.criapp.domain.repository.INotificationRepository
import com.example.criapp.models.responses.NotificationResponse
import retrofit2.HttpException

class NotificationRepositoryImpl(private val apiService: CriAppApiService) : INotificationRepository {

    override suspend fun getPendingNotifications(): Result<List<NotificationResponse>> {
        return try {
            val response = apiService.getPendingNotifications()
            if (response.success) {
                Result.success(response.data ?: emptyList())
            } else {
                Result.failure(Exception(response.Messages?.joinToString("\n") ?: "Unknown error"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(e.toErrorMessage()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsDelivered(id: String): Result<Unit> {
        return try {
            val response = apiService.markNotificationDelivered(id)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.Messages?.joinToString("\n") ?: "Unknown error"))
        } catch (e: HttpException) {
            Result.failure(Exception(e.toErrorMessage()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsRead(id: String): Result<Unit> {
        return try {
            val response = apiService.markNotificationRead(id)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.Messages?.joinToString("\n") ?: "Unknown error"))
        } catch (e: HttpException) {
            Result.failure(Exception(e.toErrorMessage()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
