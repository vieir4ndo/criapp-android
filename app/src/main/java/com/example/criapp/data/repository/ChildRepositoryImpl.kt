package com.example.criapp.data.repository
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.domain.repository.IChildRepository
import com.example.criapp.models.responses.ChildResponse
import com.example.criapp.data.remote.toErrorMessage
import com.example.criapp.data.remote.safeApiCall
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class ChildRepositoryImpl(private val apiService: CriAppApiService) : IChildRepository {

    override suspend fun getChildren(): Result<List<ChildResponse>> {
        return safeApiCall("ChildRepositoryImpl") {
            val response = apiService.getChildren()
            if (response.success) {
                Result.success(response.data?.firstOrNull() ?: emptyList())
            } else {
                Result.failure(Exception(response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)))
            }
        }
    }

    override suspend fun createChild(requestBody: String): Result<Unit> {
        return safeApiCall("ChildRepositoryImpl") {
            val body = requestBody.toRequestBody("application/json".toMediaType())
            val response = apiService.createChild(body)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)))
        }
    }

    override suspend fun updateChild(childId: String, requestBody: String): Result<Unit> {
        return safeApiCall("ChildRepositoryImpl") {
            val body = requestBody.toRequestBody("application/json".toMediaType())
            val response = apiService.updateChild(childId, body)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)))
        }
    }
}
