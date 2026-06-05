package com.example.criapp.presentation.auth
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criapp.domain.repository.IAuthRepository
import com.example.criapp.presentation.common.UiState
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val repository: IAuthRepository) : ViewModel() {

    private val _state = MutableLiveData<UiState<Unit>>()
    val state: LiveData<UiState<Unit>> = _state

    fun forgotPassword(requestBody: String) {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.forgotPassword(requestBody)
            _state.value = if (result.isSuccess) {
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }
}
