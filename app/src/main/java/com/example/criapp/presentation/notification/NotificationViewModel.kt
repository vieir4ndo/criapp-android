package com.example.criapp.presentation.notification
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criapp.domain.repository.INotificationRepository
import com.example.criapp.models.responses.NotificationResponse
import com.example.criapp.presentation.common.UiState
import kotlinx.coroutines.launch

class NotificationViewModel(private val repository: INotificationRepository) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 5
    }

    private val _state = MutableLiveData<UiState<List<NotificationResponse>>>()
    val state: LiveData<UiState<List<NotificationResponse>>> = _state

    private val _loadMoreState = MutableLiveData<UiState<List<NotificationResponse>>>()
    val loadMoreState: LiveData<UiState<List<NotificationResponse>>> = _loadMoreState

    private val _markReadState = MutableLiveData<UiState<String>>()
    val markReadState: LiveData<UiState<String>> = _markReadState

    private var currentPage = 1
    var isLastPage = false
        private set
    var isLoadingMore = false
        private set

    fun loadHistory() {
        currentPage = 1
        isLastPage = false
        _state.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getNotificationHistory(currentPage, PAGE_SIZE)
            if (result.isSuccess) {
                val data = result.getOrThrow()
                if (data.size < PAGE_SIZE) isLastPage = true
                _state.value = UiState.Success(data)
            } else {
                _state.value = UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }

    fun loadNextPage() {
        if (isLastPage || isLoadingMore) return
        isLoadingMore = true
        _loadMoreState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getNotificationHistory(currentPage + 1, PAGE_SIZE)
            isLoadingMore = false
            if (result.isSuccess) {
                val data = result.getOrThrow()
                if (data.size < PAGE_SIZE) isLastPage = true
                if (data.isNotEmpty()) currentPage++
                _loadMoreState.value = UiState.Success(data)
            } else {
                _loadMoreState.value = UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
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
