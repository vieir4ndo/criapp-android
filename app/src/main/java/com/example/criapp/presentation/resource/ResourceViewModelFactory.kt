package com.example.criapp.presentation.resource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.criapp.domain.repository.IResourceRepository

class ResourceViewModelFactory(private val repository: IResourceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ResourceViewModel(repository) as T
    }
}
