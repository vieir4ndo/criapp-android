package com.example.criapp.presentation.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.criapp.domain.repository.IRecommendationRepository

class RecommendationViewModelFactory(private val repository: IRecommendationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return RecommendationViewModel(repository) as T
    }
}
