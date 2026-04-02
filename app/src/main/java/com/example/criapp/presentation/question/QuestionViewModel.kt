package com.example.criapp.presentation.question

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criapp.domain.repository.IChildRepository
import com.example.criapp.domain.repository.IQuestionRepository
import com.example.criapp.models.responses.ChildResponse
import com.example.criapp.models.responses.QuestionResponse
import com.example.criapp.presentation.common.UiState
import kotlinx.coroutines.launch

class QuestionViewModel(
    private val childRepository: IChildRepository,
    private val questionRepository: IQuestionRepository
) : ViewModel() {

    private val _childrenState = MutableLiveData<UiState<List<ChildResponse>>>()
    val childrenState: LiveData<UiState<List<ChildResponse>>> = _childrenState

    private val _questionsState = MutableLiveData<UiState<List<QuestionResponse>>>()
    val questionsState: LiveData<UiState<List<QuestionResponse>>> = _questionsState

    private val _answerState = MutableLiveData<UiState<Unit>>()
    val answerState: LiveData<UiState<Unit>> = _answerState

    fun loadChildren() {
        _childrenState.value = UiState.Loading
        viewModelScope.launch {
            val result = childRepository.getChildren()
            _childrenState.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun loadQuestions(childId: String) {
        _questionsState.value = UiState.Loading
        viewModelScope.launch {
            val result = questionRepository.getQuestions(childId)
            _questionsState.value = if (result.isSuccess) {
                UiState.Success(result.getOrThrow())
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun answerQuestion(requestBody: String) {
        _answerState.value = UiState.Loading
        viewModelScope.launch {
            val result = questionRepository.answerQuestion(requestBody)
            _answerState.value = if (result.isSuccess) {
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }
}
