package com.example.criapp.data.repository
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.domain.repository.IProfileRepository
import com.example.criapp.models.responses.UserResponse
import com.example.criapp.data.remote.toErrorMessage
import com.example.criapp.data.remote.safeApiCall
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class ProfileRepositoryImpl(private val apiService: CriAppApiService) : IProfileRepository {

    override suspend fun getProfile(): Result<UserResponse> {
        return safeApiCall("ProfileRepositoryImpl") {
            val response = apiService.getProfile()
            if (response.success) {
                val user = response.data?.firstOrNull()
                    ?: return@safeApiCall Result.failure(Exception("Respuesta vacía del servidor"))
                Result.success(user)
            } else {
                Result.failure(
                    Exception(
                        response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)
                    )
                )
            }
        }
    }

    override suspend fun updateProfile(requestBody: String): Result<Unit> {
        return safeApiCall("ProfileRepositoryImpl") {
            val body = requestBody.toRequestBody("application/json".toMediaType())
            val response = apiService.updateProfile(body)
            if (response.success) Result.success(Unit)
            else Result.failure(
                Exception(
                    response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)
                )
            )
        }
    }

    override suspend fun deactivateAccount(): Result<Unit> {
        return safeApiCall("ProfileRepositoryImpl") {
            val response = apiService.deactivateAccount()
            if (response.success) Result.success(Unit)
            else Result.failure(
                Exception(
                    response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)
                )
            )
        }
    }
}
