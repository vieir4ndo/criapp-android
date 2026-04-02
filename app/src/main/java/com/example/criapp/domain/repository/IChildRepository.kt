package com.example.criapp.domain.repository

import com.example.criapp.models.responses.ChildResponse

interface IChildRepository {
    suspend fun getChildren(): Result<List<ChildResponse>>
    suspend fun createChild(requestBody: String): Result<Unit>
    suspend fun updateChild(childId: String, requestBody: String): Result<Unit>
}
