package com.example.criapp.models.responses

import kotlinx.serialization.Serializable

@Serializable
class LoginUserResponse {
    val token = ""
    val expiresIn = ""
    val user: UserResponse? = null;
}