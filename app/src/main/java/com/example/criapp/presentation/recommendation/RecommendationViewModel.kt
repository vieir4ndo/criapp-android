package com.example.criapp.presentation.recommendation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criapp.domain.repository.IRecommendationRepository
import com.example.criapp.models.responses.RecommendationResponse
import com.example.criapp.presentation.common.UiState
import kotlinx.coroutines.launch

class RecommendationViewModel(private val repository: IRecommendationRepository) : ViewModel() {
    private val _state = MutableLiveData<UiState<List<RecommendationResponse>>>()
    val state: LiveData<UiState<List<RecommendationResponse>>> = _state

    private val _checkpointState = MutableLiveData<UiState<Int>>()
    val checkpointState: LiveData<UiState<Int>> = _checkpointState

    fun loadRecommendations(childId: String) {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getRecommendations(childId)
            _state.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun registerCheckpoint(childId: String, recommendationId: Int, completed: Boolean) {
        _checkpointState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.registerCheckpoint(childId, recommendationId, completed)
            _checkpointState.value = if (result.isSuccess) {
                UiState.Success(recommendationId)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }
}
