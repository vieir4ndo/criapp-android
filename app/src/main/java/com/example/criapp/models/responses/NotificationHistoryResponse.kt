package com.example.criapp.models.responses

data class NotificationHistoryResponse(
    val items: List<NotificationResponse>,
    val totalCount: Int,
    val page: Int,
    val pageSize: Int
)
