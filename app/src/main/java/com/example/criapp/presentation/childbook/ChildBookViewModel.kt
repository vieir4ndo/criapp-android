package com.example.criapp.presentation.childbook
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criapp.domain.repository.IChildBookRepository
import com.example.criapp.models.responses.ChildBookSummaryResponse
import com.example.criapp.presentation.common.UiState
import kotlinx.coroutines.launch

class ChildBookViewModel(private val repository: IChildBookRepository) : ViewModel() {

    private val _childBookState = MutableLiveData<UiState<ChildBookSummaryResponse?>>()
    val childBookState: LiveData<UiState<ChildBookSummaryResponse?>> = _childBookState

    fun loadChildBook(childBookId: String) {
        _childBookState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getChildBookScore(childBookId)
            _childBookState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }
}
