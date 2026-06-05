package com.example.criapp.presentation.award
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criapp.domain.repository.IAwardRepository
import com.example.criapp.models.responses.AwardResponse
import com.example.criapp.presentation.common.UiState
import kotlinx.coroutines.launch

class AwardViewModel(private val repository: IAwardRepository) : ViewModel() {
    private val _state = MutableLiveData<UiState<List<AwardResponse>>>()
    val state: LiveData<UiState<List<AwardResponse>>> = _state

    fun loadAwards(childId: String) {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getAwards(childId)
            _state.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }
}