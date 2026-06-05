package com.example.criapp.data.remote

import com.example.criapp.models.requests.CreateCheckpointRequest
import com.example.criapp.models.requests.LoginUserRequest
import com.example.criapp.models.requests.RefreshTokenRequest
import com.example.criapp.models.responses.AudiovisualResourceResponse
import com.example.criapp.models.responses.AwardResponse
import com.example.criapp.models.responses.ChildBookSummaryResponse
import com.example.criapp.models.responses.ChildResponse
import com.example.criapp.models.responses.RecommendationResponse
import com.example.criapp.models.responses.NotificationResponse
import com.example.criapp.models.responses.CommentResponse
import com.example.criapp.models.responses.FeatureFlagsResponse
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

    // ── Feature Flags ────────────────────────────────────────────────────────

    @GET("feature-flags")
    suspend fun getFeatureFlags(): ApiResponse<List<FeatureFlagsResponse>>

    // ── Authentication ──────────────────────────────────────────────────────

    @POST("auth/login")
    suspend fun login(@Body request: LoginUserRequest): ApiResponse<List<LoginUserResponse>>

    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponse<List<LoginUserResponse>>

    @POST("user")
    suspend fun createUser(@Body body: RequestBody): ApiResponse<List<Any>>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body body: RequestBody): ApiResponse<List<Any>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body body: RequestBody): ApiResponse<List<Any>>

    // ── Profile ─────────────────────────────────────────────────────────────

    @GET("user")
    suspend fun getProfile(): ApiResponse<List<UserResponse>>

    @PATCH("user")
    suspend fun updateProfile(@Body body: RequestBody): ApiResponse<List<Any>>

    @DELETE("user")
    suspend fun deactivateAccount(): ApiResponse<List<Any>>

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

    // ── Child Book ──────────────────────────────────────────────────────
    @GET("child/{childId}/book/score")
    suspend fun getChildBookScore(@Path("childId") childId: String): ApiResponse<List<ChildBookSummaryResponse>>

    // ── Awards ──────────────────────────────────────────────────────
    @GET("child/{childId}/book/awards")
    suspend fun getAwards(@Path("childId") childId: String): ApiResponse<List<AwardResponse>>

    // ── Recommendations ──────────────────────────────────────────────
    @GET("child/{childId}/book/recommendations")
    suspend fun getRecommendations(@Path("childId") childId: String): ApiResponse<List<RecommendationResponse>>

    @POST("child/{childId}/book/recommendations/{recommendationId}/checkpoint")
    suspend fun registerCheckpoint(
        @Path("childId") childId: String,
        @Path("recommendationId") recommendationId: Int,
        @Body request: CreateCheckpointRequest
    ): ApiResponse<List<Any>>

    // ── Notifications ─────────────────────────────────────────────────────

    @GET("notifications/pending")
    suspend fun getPendingNotifications(): ApiResponse<List<NotificationResponse>>

    @GET("notifications/history")
    suspend fun getNotificationHistory(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): ApiResponse<List<NotificationResponse>>

    @PATCH("notifications/{id}/delivered")
    suspend fun markNotificationDelivered(@Path("id") id: String): ApiResponse<List<Any>>

    @PATCH("notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: String): ApiResponse<List<Any>>
}
