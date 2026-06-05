package com.example.criapp.presentation.faq
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criapp.domain.repository.IFaqRepository
import com.example.criapp.models.responses.FaqResponse
import com.example.criapp.presentation.common.UiState
import kotlinx.coroutines.launch

class FaqViewModel(private val repository: IFaqRepository) : ViewModel() {

    private val _state = MutableLiveData<UiState<List<FaqResponse>>>()
    val state: LiveData<UiState<List<FaqResponse>>> = _state

    fun loadFaqs() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getFaqs()
            _state.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }
}
