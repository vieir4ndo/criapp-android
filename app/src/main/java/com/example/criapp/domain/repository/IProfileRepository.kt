package com.example.criapp.domain.repository

import com.example.criapp.models.responses.UserResponse

interface IProfileRepository {
    suspend fun getProfile(): Result<UserResponse>
    suspend fun updateProfile(requestBody: String): Result<Unit>
    suspend fun deactivateAccount(): Result<Unit>
}
