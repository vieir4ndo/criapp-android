package com.example.criapp.data.local

import android.content.Context

class PreferencesManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val appStatePrefs = context.getSharedPreferences(APP_STATE_PREFS_NAME, Context.MODE_PRIVATE)

    fun getAuthorizationHeader(): String = "Bearer ${prefs.getString(KEY_TOKEN, "") ?: ""}"

    fun getUserId(): String = prefs.getString(KEY_USER_ID, "") ?: ""

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""

    fun saveAuthData(token: String, expiresIn: String, userId: String?, userName: String?) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_EXPIRES_IN, expiresIn)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
            apply()
        }
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    fun hasCompletedOnboarding(): Boolean =
        appStatePrefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)

    fun markOnboardingCompleted() {
        appStatePrefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
    }

    companion object {
        private const val PREFS_NAME = "ApiCredentials"
        private const val APP_STATE_PREFS_NAME = "AppState"
        private const val KEY_TOKEN = "token"
        private const val KEY_EXPIRES_IN = "token_expires_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
