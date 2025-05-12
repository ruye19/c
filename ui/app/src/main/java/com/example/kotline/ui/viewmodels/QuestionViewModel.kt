package com.example.kotline.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotline.ui.api.ApiClient
import com.example.kotline.ui.api.Question
import com.example.kotline.ui.api.QuestionApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class QuestionState {
    object Idle : QuestionState()
    object Loading : QuestionState()
    data class Success(val questions: List<Question>) : QuestionState()
    data class Error(val message: String) : QuestionState()
}

class QuestionViewModel : ViewModel() {
    private val _questionState = MutableStateFlow<QuestionState>(QuestionState.Idle)
    val questionState: StateFlow<QuestionState> = _questionState

    private val questionApi = ApiClient.create(QuestionApi::class.java)

    fun fetchQuestions() {
        viewModelScope.launch {
            _questionState.value = QuestionState.Loading
            try {
                val response = questionApi.getAllQuestions()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _questionState.value = QuestionState.Success(it.allQuestion)
                    } ?: run {
                        _questionState.value = QuestionState.Error("Empty response from server")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("QuestionViewModel", "Failed to fetch questions: ${errorBody ?: response.message()}")
                    _questionState.value = QuestionState.Error(
                        "Failed to fetch questions: ${errorBody ?: response.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("QuestionViewModel", "Network error", e)
                _questionState.value = QuestionState.Error(
                    "Network error: ${e.message ?: "Unknown error occurred"}"
                )
            }
        }
    }
} 