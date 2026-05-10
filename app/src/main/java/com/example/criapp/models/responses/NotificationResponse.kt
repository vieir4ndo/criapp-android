package com.example.criapp.models.responses

data class NotificationResponse(
    val id: String,
    val childBookId: String,
    val type: Int,
    val date: String,
    val read: Boolean,
    val title: String,
    val body: String
)
