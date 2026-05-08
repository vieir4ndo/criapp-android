package com.example.criapp.data.remote

import com.example.criapp.models.requests.LoginUserRequest
import com.example.criapp.models.responses.AudiovisualResourceResponse
import com.example.criapp.models.responses.AwardResponse
import com.example.criapp.models.responses.ChildResponse
import com.example.criapp.models.responses.CommentResponse
import com.example.criapp.models.responses.FaqResponse
import com.example.criapp.models.responses.ForumResponse
import com.example.criapp.models.responses.LoginUserResponse
import com.example.criapp.models.responses.QuestionResponse
import com.example.criapp.models.responses.UserResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CriAppApiService {

    // ── Authentication ──────────────────────────────────────────────────────

    @POST("user/login")
    suspend fun login(@Body request: LoginUserRequest): ApiResponse<List<LoginUserResponse>>

    @POST("user")
    suspend fun createUser(@Body body: RequestBody): ApiResponse<List<Any>>

    @POST("user/forgot-password")
    suspend fun forgotPassword(@Body body: RequestBody): ApiResponse<List<Any>>

    @POST("user/reset-password")
    suspend fun resetPassword(@Body body: RequestBody): ApiResponse<List<Any>>

    // ── Profile ─────────────────────────────────────────────────────────────

    @GET("user")
    suspend fun getProfile(): ApiResponse<List<UserResponse>>

    @PATCH("user")
    suspend fun updateProfile(@Body body: RequestBody): ApiResponse<List<Any>>

    // ── Child ───────────────────────────────────────────────────────────────

    @GET("child")
    suspend fun getChildren(): ApiResponse<List<List<ChildResponse>>>

    @POST("child")
    suspend fun createChild(@Body body: RequestBody): ApiResponse<List<Any>>

    @PATCH("child/{id}")
    suspend fun updateChild(
        @Path("id") id: String,
        @Body body: RequestBody
    ): ApiResponse<List<Any>>

    // ── Questions and Answers ─────────────────────────────────────────────

    @GET("question")
    suspend fun getQuestions(@Query("childId") childId: String): ApiResponse<List<QuestionResponse>>

    @POST("answer")
    suspend fun answerQuestion(@Body body: RequestBody): ApiResponse<List<Any>>

    // ── FAQ (public) ──────────────────────────────────────────────────────

    @GET("faq")
    suspend fun getFaqs(): ApiResponse<List<FaqResponse>>

    // ── Recursos audiovisuales (público) ──────────────────────────────────

    @GET("audiovisual-resource")
    suspend fun getResources(): ApiResponse<List<AudiovisualResourceResponse>>

    // ── Forums ─────────────────────────────────────────────────────────────

    @GET("forum")
    suspend fun getForums(): ApiResponse<List<ForumResponse>>

    @POST("forum")
    suspend fun createForum(@Body body: RequestBody): ApiResponse<List<Any>>

    @PATCH("forum/{id}")
    suspend fun updateForum(
        @Path("id") id: String,
        @Body body: RequestBody
    ): ApiResponse<List<Any>>

    @GET("forum/{id}/comments")
    suspend fun getComments(@Path("id") id: String): ApiResponse<List<CommentResponse>>

    @POST("comment")
    suspend fun createComment(@Body body: RequestBody): ApiResponse<List<Any>>

    @DELETE("comment/{id}")
    suspend fun deleteComment(@Path("id") id: String): ApiResponse<List<Any>>

    // ── Awards ──────────────────────────────────────────────────────
    @GET("user/awards")
    suspend fun getAwards(): ApiResponse<List<AwardResponse>>
}
