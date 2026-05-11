package com.example.criapp.presentation.childbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.criapp.domain.repository.IChildBookRepository

class ChildBookViewModelFactory(private val repository: IChildBookRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ChildBookViewModel(repository) as T
    }
}
