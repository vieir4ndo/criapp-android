package com.example.criapp.models.responses
data class UserResponse(
    val id: String,
    val name: String,
    val birthDate: String,
    val sex: Int,
    val email: String,
    val active: Boolean,
    val numberOfChildren: Int,
    val familyType: Int,
    val educationalLevel: Int,
    val isAdmin: Boolean
)
