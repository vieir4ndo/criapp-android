package com.example.criapp.domain.repository

import com.example.criapp.models.responses.CommentResponse
import com.example.criapp.models.responses.ForumResponse

interface IForumRepository {
    suspend fun getForums(): Result<List<ForumResponse>>
    suspend fun createForum(requestBody: String): Result<Unit>
    suspend fun updateForum(forumId: String, requestBody: String): Result<Unit>
    suspend fun getComments(forumId: String): Result<List<CommentResponse>>
    suspend fun createComment(requestBody: String): Result<Unit>
    suspend fun deleteComment(commentId: String): Result<Unit>
}
