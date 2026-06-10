package com.example.criapp.data.repository
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.domain.repository.IForumRepository
import com.example.criapp.models.responses.CommentResponse
import com.example.criapp.models.responses.ForumResponse
import com.example.criapp.data.remote.toErrorMessage
import com.example.criapp.data.remote.safeApiCall
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class ForumRepositoryImpl(private val apiService: CriAppApiService) : IForumRepository {

    override suspend fun getForums(): Result<List<ForumResponse>> {
        return safeApiCall("ForumRepositoryImpl") {
            val response = apiService.getForums()
            if (response.success) {
                Result.success(response.data ?: emptyList())
            } else {
                Result.failure(Exception(response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)))
            }
        }
    }

    override suspend fun createForum(requestBody: String): Result<Unit> {
        return safeApiCall("ForumRepositoryImpl") {
            val body = requestBody.toRequestBody("application/json".toMediaType())
            val response = apiService.createForum(body)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)))
        }
    }

    override suspend fun updateForum(forumId: String, requestBody: String): Result<Unit> {
        return safeApiCall("ForumRepositoryImpl") {
            val body = requestBody.toRequestBody("application/json".toMediaType())
            val response = apiService.updateForum(forumId, body)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)))
        }
    }

    override suspend fun getComments(forumId: String): Result<List<CommentResponse>> {
        return safeApiCall("ForumRepositoryImpl") {
            val response = apiService.getComments(forumId)
            if (response.success) {
                Result.success(response.data ?: emptyList())
            } else {
                Result.failure(Exception(response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)))
            }
        }
    }

    override suspend fun createComment(requestBody: String): Result<Unit> {
        return safeApiCall("ForumRepositoryImpl") {
            val body = requestBody.toRequestBody("application/json".toMediaType())
            val response = apiService.createComment(body)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)))
        }
    }

    override suspend fun deleteComment(commentId: String): Result<Unit> {
        return safeApiCall("ForumRepositoryImpl") {
            val response = apiService.deleteComment(commentId)
            if (response.success) Result.success(Unit)
            else Result.failure(Exception(response.Messages?.joinToString("\n") ?: CRIAppApplication.context.getString(R.string.error_unknown)))
        }
    }
}
