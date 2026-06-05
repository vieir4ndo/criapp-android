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

class SignUpViewModel(private val repository: IAuthRepository) : ViewModel() {

    private val _signUpState = MutableLiveData<UiState<Unit>>()
    val signUpState: LiveData<UiState<Unit>> = _signUpState

    fun createUser(requestBody: String) {
        _signUpState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.createUser(requestBody)
            _signUpState.value = if (result.isSuccess) {
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }
}
