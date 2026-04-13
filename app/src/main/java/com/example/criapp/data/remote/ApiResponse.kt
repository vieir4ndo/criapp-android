package com.example.criapp.data.remote

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val Messages: List<String>?
)
