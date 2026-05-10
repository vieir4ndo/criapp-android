package com.example.criapp.presentation.advice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criapp.domain.repository.IAdviceRepository
import com.example.criapp.models.responses.AdviceResponse
import com.example.criapp.presentation.common.UiState
import kotlinx.coroutines.launch

class AdviceViewModel(private val repository: IAdviceRepository) : ViewModel() {
    private val _state = MutableLiveData<UiState<List<AdviceResponse>>>()
    val state: LiveData<UiState<List<AdviceResponse>>> = _state

    fun loadAdvices() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getAdvices()
            _state.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }
}
