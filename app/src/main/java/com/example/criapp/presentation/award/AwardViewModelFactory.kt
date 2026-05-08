package com.example.criapp.presentation.award

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.criapp.domain.repository.IAwardRepository

class AwardViewModelFactory(private val repository: IAwardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AwardViewModel(repository) as T
    }
}