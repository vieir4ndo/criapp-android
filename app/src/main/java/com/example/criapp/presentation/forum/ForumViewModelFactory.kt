package com.example.criapp.presentation.forum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.criapp.domain.repository.IForumRepository

class ForumViewModelFactory(private val repository: IForumRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ForumViewModel(repository) as T
    }
}
