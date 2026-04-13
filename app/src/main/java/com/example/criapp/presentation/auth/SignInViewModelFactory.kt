package com.example.criapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.criapp.domain.repository.IAuthRepository

class SignInViewModelFactory(private val repository: IAuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SignInViewModel(repository) as T
    }
}
