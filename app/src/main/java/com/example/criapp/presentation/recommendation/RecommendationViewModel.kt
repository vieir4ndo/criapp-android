package com.example.criapp.presentation.recommendation
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

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

    private val _checkpointState = MutableLiveData<UiState<Boolean>>()
    val checkpointState: LiveData<UiState<Boolean>> = _checkpointState

    fun loadRecommendationsByChildBookId(childBookId: String) {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getRecommendationsByChildBookId(childBookId)
            _state.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }

    fun registerCheckpoint(childBookId: String, childBookRecommendationId: String, completed: Boolean) {
        _checkpointState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.registerCheckpoint(childBookId, childBookRecommendationId, completed)
            _checkpointState.value = if (result.isSuccess) {
                UiState.Success(result.getOrDefault(false))
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }
}
