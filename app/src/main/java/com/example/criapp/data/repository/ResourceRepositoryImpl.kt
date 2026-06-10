package com.example.criapp.data.repository
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.data.remote.toErrorMessage
import com.example.criapp.data.remote.safeApiCall
import com.example.criapp.domain.repository.IResourceRepository
import com.example.criapp.models.responses.AudiovisualResourceResponse

class ResourceRepositoryImpl(private val apiService: CriAppApiService) : IResourceRepository {

    override suspend fun getResources(): Result<List<AudiovisualResourceResponse>> {
        return safeApiCall("ResourceRepositoryImpl") {
            val response = apiService.getResources()
            if (response.success) {
                Result.success(response.data ?: emptyList())
            } else {
                Result.failure(Exception(response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)))
            }
        }
    }
}
