package com.example.criapp.services

object FeatureFlagsManager {
    var phoneNumberUsernameEnabled: Boolean = false
        private set

    fun update(phoneNumberUsernameEnabled: Boolean) {
        this.phoneNumberUsernameEnabled = phoneNumberUsernameEnabled
    }
}
