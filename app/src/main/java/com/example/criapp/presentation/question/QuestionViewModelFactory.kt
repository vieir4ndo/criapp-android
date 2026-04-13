package com.example.criapp.presentation.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.criapp.domain.repository.IChildRepository
import com.example.criapp.domain.repository.IQuestionRepository

class QuestionViewModelFactory(
    private val childRepository: IChildRepository,
    private val questionRepository: IQuestionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return QuestionViewModel(childRepository, questionRepository) as T
    }
}
