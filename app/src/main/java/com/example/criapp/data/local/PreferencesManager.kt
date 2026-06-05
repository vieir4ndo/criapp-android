package com.example.criapp.data.local

import android.content.Context
import java.time.OffsetDateTime

class PreferencesManager(context: Context) {

    val applicationContext: Context = context.applicationContext

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val appStatePrefs = context.getSharedPreferences(APP_STATE_PREFS_NAME, Context.MODE_PRIVATE)

    fun getAuthorizationHeader(): String = "Bearer ${prefs.getString(KEY_TOKEN, "") ?: ""}"

    fun getUserId(): String = prefs.getString(KEY_USER_ID, "") ?: ""

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""

    fun getRefreshToken(): String = prefs.getString(KEY_REFRESH_TOKEN, "") ?: ""

    fun saveAuthData(
        token: String,
        expiresIn: String,
        userId: String?,
        userName: String?,
        refreshToken: String,
        refreshTokenExpiresIn: String
    ) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_EXPIRES_IN, expiresIn)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_REFRESH_TOKEN_EXPIRES_IN, refreshTokenExpiresIn)
            apply()
        }
    }

    fun updateTokens(token: String, expiresIn: String, refreshToken: String, refreshTokenExpiresIn: String) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_EXPIRES_IN, expiresIn)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_REFRESH_TOKEN_EXPIRES_IN, refreshTokenExpiresIn)
            apply()
        }
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    fun isSessionActive(): Boolean {
        val token = prefs.getString(KEY_TOKEN, "")
        if (token.isNullOrEmpty()) return false
        val expiresIn = prefs.getString(KEY_EXPIRES_IN, "") ?: return false
        return try {
            OffsetDateTime.parse(expiresIn).isAfter(OffsetDateTime.now())
        } catch (e: Exception) {
            false
        }
    }

    fun hasCompletedOnboarding(userId: String): Boolean =
        appStatePrefs.getBoolean("${KEY_ONBOARDING_COMPLETED}_$userId", false)

    fun markOnboardingCompleted(userId: String) {
        appStatePrefs.edit().putBoolean("${KEY_ONBOARDING_COMPLETED}_$userId", true).apply()
    }

    companion object {
        private const val PREFS_NAME = "ApiCredentials"
        private const val APP_STATE_PREFS_NAME = "AppState"
        private const val KEY_TOKEN = "token"
        private const val KEY_EXPIRES_IN = "token_expires_in"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_REFRESH_TOKEN_EXPIRES_IN = "refresh_token_expires_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
