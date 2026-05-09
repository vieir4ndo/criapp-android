package com.example.criapp.data.repository

import com.example.criapp.data.remote.CriAppHttpClient
import com.example.criapp.domain.repository.IForumRepository
import com.example.criapp.models.responses.CommentResponse
import com.example.criapp.models.responses.ForumResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ForumRepositoryImpl(private val httpClient: CriAppHttpClient) : IForumRepository {

    private val gson = Gson()

    override suspend fun getForums(): Result<List<ForumResponse>> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.getAuthenticated("/forum") }
            if (response != null && response.success) {
                val json = gson.toJson(response.data)
                Result.success(gson.fromJson(json, Array<ForumResponse>::class.java).toList())
            } else {
                Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createForum(requestBody: String): Result<Unit> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.postAuthenticated("/forum", requestBody) }
            if (response != null && response.success) Result.success(Unit)
            else Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateForum(forumId: String, requestBody: String): Result<Unit> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.patchAuthenticated("/forum/$forumId", requestBody) }
            if (response != null && response.success) Result.success(Unit)
            else Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getComments(forumId: String): Result<List<CommentResponse>> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.getAuthenticated("/forum/$forumId/comments") }
            if (response != null && response.success) {
                val json = gson.toJson(response.data)
                Result.success(gson.fromJson(json, Array<CommentResponse>::class.java).toList())
            } else {
                Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createComment(requestBody: String): Result<Unit> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.postAuthenticated("/comment", requestBody) }
            if (response != null && response.success) Result.success(Unit)
            else Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteComment(commentId: String): Result<Unit> {
        return try {
            val response = withContext(Dispatchers.IO) { httpClient.deleteAuthenticated("/comment/$commentId") }
            if (response != null && response.success) Result.success(Unit)
            else Result.failure(Exception(response?.Messages?.joinToString("\n") ?: "Error desconocido"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
