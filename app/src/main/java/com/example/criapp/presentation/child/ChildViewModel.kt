package com.example.criapp.presentation.child
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criapp.domain.repository.IChildRepository
import com.example.criapp.models.responses.ChildResponse
import com.example.criapp.presentation.common.UiState
import kotlinx.coroutines.launch

class ChildViewModel(private val repository: IChildRepository) : ViewModel() {

    private val _childrenState = MutableLiveData<UiState<List<ChildResponse>>>()
    val childrenState: LiveData<UiState<List<ChildResponse>>> = _childrenState

    private val _createState = MutableLiveData<UiState<Unit>>()
    val createState: LiveData<UiState<Unit>> = _createState

    private val _updateState = MutableLiveData<UiState<Unit>>()
    val updateState: LiveData<UiState<Unit>> = _updateState

    fun loadChildren() {
        _childrenState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getChildren()
            _childrenState.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }

    fun createChild(requestBody: String) {
        _createState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.createChild(requestBody)
            _createState.value = if (result.isSuccess) {
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }

    fun updateChild(childId: String, requestBody: String) {
        _updateState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.updateChild(childId, requestBody)
            _updateState.value = if (result.isSuccess) {
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }
}
