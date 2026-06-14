package com.example.criapp.models.responses

data class RecommendationResponse(
    val id: Int,
    val childBookRecommendationId: String,
    val text: String,
    val isCompletedToday: Boolean?,
    val createdAt: String?,
    val completedCountInWindow: Int
)
