package com.example.criapp.models.responses

import com.google.gson.annotations.SerializedName

data class FeatureFlagsResponse(
    @SerializedName("phoneNumberUsernameEnabled")
    val phoneNumberUsernameEnabled: Boolean
)
