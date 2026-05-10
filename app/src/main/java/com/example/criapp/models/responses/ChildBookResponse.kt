package com.example.criapp.models.responses

data class ChildBookResponse(
    val id: String,
    val childId: String,
    val score: Int,
    val scoreLastUpdatedAt: String,
    val awards: List<AwardResponse>,
    val advices: List<AdviceResponse>
)
