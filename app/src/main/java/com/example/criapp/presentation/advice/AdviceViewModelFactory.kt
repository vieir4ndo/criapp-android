package com.example.criapp.presentation.advice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.criapp.domain.repository.IAdviceRepository

class AdviceViewModelFactory(private val repository: IAdviceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AdviceViewModel(repository) as T
    }
}
