package com.example.criapp.domain.repository

import com.example.criapp.models.responses.LoginUserResponse

interface IAuthRepository {
    suspend fun login(username: String, password: String): Result<LoginUserResponse>
    suspend fun createUser(requestBody: String): Result<Unit>
    suspend fun forgotPassword(requestBody: String): Result<Unit>
    suspend fun resetPassword(requestBody: String): Result<Unit>
}
