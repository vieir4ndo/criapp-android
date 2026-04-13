package com.example.criapp.domain.repository

import com.example.criapp.models.responses.AudiovisualResourceResponse

interface IResourceRepository {
    suspend fun getResources(): Result<List<AudiovisualResourceResponse>>
}
