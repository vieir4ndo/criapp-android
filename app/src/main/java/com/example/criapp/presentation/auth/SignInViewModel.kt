package com.example.criapp.presentation.auth
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criapp.domain.repository.IAuthRepository
import com.example.criapp.models.responses.FeatureFlagsResponse
import com.example.criapp.models.responses.LoginUserResponse
import com.example.criapp.presentation.common.UiState
import com.example.criapp.services.FeatureFlagsManager
import kotlinx.coroutines.launch

class SignInViewModel(private val repository: IAuthRepository) : ViewModel() {

    private val _loginState = MutableLiveData<UiState<LoginUserResponse>>()
    val loginState: LiveData<UiState<LoginUserResponse>> = _loginState

    private val _featureFlagsState = MutableLiveData<UiState<FeatureFlagsResponse>>()
    val featureFlagsState: LiveData<UiState<FeatureFlagsResponse>> = _featureFlagsState

    fun loadFeatureFlags() {
        viewModelScope.launch {
            val result = repository.getFeatureFlags()
            if (result.isSuccess) {
                val flags = result.getOrThrow()
                FeatureFlagsManager.update(flags.phoneNumberUsernameEnabled)
                _featureFlagsState.value = UiState.Success(flags)
            }
        }
    }

    fun login(username: String, password: String) {
        _loginState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.login(username, password)
            _loginState.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }
}
