package com.example.criapp.models.responses

data class RecommendationResponse(
    val id: Int,
    val text: String,
    val image: String?,
    val todayCompleted: Boolean?,
    val createdAt: String?,
    val completedCheckpoints: Int
)
