package com.example.criapp.presentation.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criapp.domain.repository.INotificationRepository
import com.example.criapp.models.responses.NotificationResponse
import com.example.criapp.presentation.common.UiState
import kotlinx.coroutines.launch

class NotificationViewModel(private val repository: INotificationRepository) : ViewModel() {

    private val _state = MutableLiveData<UiState<List<NotificationResponse>>>()
    val state: LiveData<UiState<List<NotificationResponse>>> = _state

    private val _markReadState = MutableLiveData<UiState<String>>()
    val markReadState: LiveData<UiState<String>> = _markReadState

    fun loadHistory() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getNotificationHistory()
            _state.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            val result = repository.markAsRead(id)
            if (result.isSuccess) {
                _markReadState.value = UiState.Success(id)
            }
        }
    }
}
