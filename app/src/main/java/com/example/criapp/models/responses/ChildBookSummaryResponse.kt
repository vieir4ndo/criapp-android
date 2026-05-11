package com.example.criapp.models.responses

data class ChildBookSummaryResponse(
    val id: String,
    val childId: String,
    val score: Int,
    val scoreLastUpdatedAt: String
)
