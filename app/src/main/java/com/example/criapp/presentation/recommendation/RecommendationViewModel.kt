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
}
