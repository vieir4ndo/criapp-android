package com.example.criapp.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criapp.domain.repository.IProfileRepository
import com.example.criapp.models.responses.UserResponse
import com.example.criapp.presentation.common.UiState
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: IProfileRepository) : ViewModel() {

    private val _profileState = MutableLiveData<UiState<UserResponse>>()
    val profileState: LiveData<UiState<UserResponse>> = _profileState

    private val _updateState = MutableLiveData<UiState<Unit>>()
    val updateState: LiveData<UiState<Unit>> = _updateState

    fun loadProfile() {
        _profileState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getProfile()
            _profileState.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun updateProfile(requestBody: String) {
        _updateState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.updateProfile(requestBody)
            _updateState.value = if (result.isSuccess) {
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun deactivateAccount() {
        _updateState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.deactivateAccount()
            _updateState.value = if (result.isSuccess) {
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }
}
