package com.example.criapp.presentation.child

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.criapp.domain.repository.IChildRepository

class ChildViewModelFactory(private val repository: IChildRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ChildViewModel(repository) as T
    }
}
