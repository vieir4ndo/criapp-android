package com.example.criapp.data.repository

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.domain.repository.IAuthRepository
import com.example.criapp.models.requests.LoginUserRequest
import com.example.criapp.models.requests.RefreshTokenRequest
import com.example.criapp.models.responses.FeatureFlagsResponse
import com.example.criapp.models.responses.LoginUserResponse
import com.example.criapp.data.remote.toErrorMessage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class AuthRepositoryImpl(private val apiService: CriAppApiService) : IAuthRepository {

    override suspend fun getFeatureFlags(): Result<FeatureFlagsResponse> {
        return try {
            val response = apiService.getFeatureFlags()
            if (response.success) {
                val flags = response.data?.firstOrNull()
                    ?: return Result.failure(Exception("Respuesta vacía del servidor"))
                Result.success(flags)
            } else {
                Result.failure(Exception(response.Messages?.joinToString("\n") ?: "Error desconocido"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(e.toErrorMessage()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(username: String, password: String): Result<LoginUserResponse> {
        return try {
            val response = apiService.login(LoginUserRequest(username, password))
            if (response.success) {
                val loginData = response.data?.firstOrNull()
                    ?: return Result.failure(Exception("Respuesta vacía del servidor"))
                Result.success(loginData)
            } else {
                Result.failure(Exception(response.Messages?.joinToString("\n") ?: "Error desconocido"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(e.toErrorMessage()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshToken(refreshToken: String): Result<LoginUserResponse> {
        return try {
            val response = apiService.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.success) {
                val data = response.data?.firstOrNull()
                    ?: return Result.failure(Exception("Respuesta vacía del servidor"))
                Result.success(data)
            } else {
                Result.failure(Exception(response.Messages?.joinToString("\n") ?: "Error desconocido"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(e.toErrorMessage()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createUser(requestBody: String): Result<Unit> {
        return try {
            val body = requestBody.toRequestBody("application/json".toMediaType())
            val response = apiService.createUser(body)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.Messages?.joinToString("\n") ?: "Error desconocido"))
        } catch (e: HttpException) {
            Result.failure(Exception(e.toErrorMessage()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun forgotPassword(requestBody: String): Result<Unit> {
        return try {
            val body = requestBody.toRequestBody("application/json".toMediaType())
            val response = apiService.forgotPassword(body)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.Messages?.joinToString("\n") ?: "Error desconocido"))
        } catch (e: HttpException) {
            Result.failure(Exception(e.toErrorMessage()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(requestBody: String): Result<Unit> {
        return try {
            val body = requestBody.toRequestBody("application/json".toMediaType())
            val response = apiService.resetPassword(body)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.Messages?.joinToString("\n") ?: "Error desconocido"))
        } catch (e: HttpException) {
            Result.failure(Exception(e.toErrorMessage()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
