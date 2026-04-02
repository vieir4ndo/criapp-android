package com.example.criapp.presentation.faq

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.criapp.domain.repository.IFaqRepository

class FaqViewModelFactory(private val repository: IFaqRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FaqViewModel(repository) as T
    }
}
