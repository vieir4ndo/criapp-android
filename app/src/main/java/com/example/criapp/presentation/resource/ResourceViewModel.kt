package com.example.criapp.presentation.resource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criapp.domain.repository.IResourceRepository
import com.example.criapp.models.responses.AudiovisualResourceResponse
import com.example.criapp.presentation.common.UiState
import kotlinx.coroutines.launch

class ResourceViewModel(private val repository: IResourceRepository) : ViewModel() {

    private val _state = MutableLiveData<UiState<List<AudiovisualResourceResponse>>>()
    val state: LiveData<UiState<List<AudiovisualResourceResponse>>> = _state

    fun loadResources() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getResources()
            _state.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }
}
