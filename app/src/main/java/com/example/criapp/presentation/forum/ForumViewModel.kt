package com.example.criapp.presentation.forum
import com.example.criapp.CRIAppApplication
import com.example.criapp.R

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criapp.domain.repository.IForumRepository
import com.example.criapp.models.responses.CommentResponse
import com.example.criapp.models.responses.ForumResponse
import com.example.criapp.presentation.common.UiState
import kotlinx.coroutines.launch

class ForumViewModel(private val repository: IForumRepository) : ViewModel() {

    private val _forumsState = MutableLiveData<UiState<List<ForumResponse>>>()
    val forumsState: LiveData<UiState<List<ForumResponse>>> = _forumsState

    private val _createForumState = MutableLiveData<UiState<Unit>>()
    val createForumState: LiveData<UiState<Unit>> = _createForumState

    private val _updateForumState = MutableLiveData<UiState<Unit>>()
    val updateForumState: LiveData<UiState<Unit>> = _updateForumState

    private val _commentsState = MutableLiveData<UiState<List<CommentResponse>>>()
    val commentsState: LiveData<UiState<List<CommentResponse>>> = _commentsState

    private val _commentActionState = MutableLiveData<UiState<Unit>>()
    val commentActionState: LiveData<UiState<Unit>> = _commentActionState

    fun loadForums() {
        _forumsState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getForums()
            _forumsState.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }

    fun createForum(requestBody: String) {
        _createForumState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.createForum(requestBody)
            _createForumState.value = if (result.isSuccess) {
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }

    fun updateForum(forumId: String, requestBody: String) {
        _updateForumState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.updateForum(forumId, requestBody)
            _updateForumState.value = if (result.isSuccess) {
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }

    fun loadComments(forumId: String) {
        _commentsState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getComments(forumId)
            _commentsState.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }

    fun createComment(requestBody: String) {
        _commentActionState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.createComment(requestBody)
            _commentActionState.value = if (result.isSuccess) {
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }

    fun deleteComment(commentId: String) {
        _commentActionState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.deleteComment(commentId)
            _commentActionState.value = if (result.isSuccess) {
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: CRIAppApplication.context.getString(R.string.error_unknown))
            }
        }
    }
}
