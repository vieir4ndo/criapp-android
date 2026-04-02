package com.example.criapp.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criapp.domain.repository.IAuthRepository
import com.example.criapp.models.responses.LoginUserResponse
import com.example.criapp.presentation.common.UiState
import kotlinx.coroutines.launch

class SignInViewModel(private val repository: IAuthRepository) : ViewModel() {

    private val _loginState = MutableLiveData<UiState<LoginUserResponse>>()
    val loginState: LiveData<UiState<LoginUserResponse>> = _loginState

    fun login(username: String, password: String) {
        _loginState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.login(username, password)
            _loginState.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }
}
