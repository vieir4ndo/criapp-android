package com.example.criapp.model

import kotlinx.serialization.Serializable

@Serializable
class LoginUserResponse {
    val token = ""
    val expiresIn = ""
    val user: UserModel? = null;
}