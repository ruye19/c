package com.example.kotline.ui.api

import retrofit2.Response
import retrofit2.http.GET

// Data class for a question
// Matches backend: id, questionid, userid, title, description, tag, username, profession

data class Question(
    val id: Int,
    val questionid: String,
    val userid: String,
    val title: String,
    val description: String,
    val tag: String?,
    val username: String,
    val profession: String
)

data class QuestionsResponse(
    val msg: String,
    val allQuestion: List<Question>
)

interface QuestionApi {
    @GET("api/question/")
    suspend fun getAllQuestions(): Response<QuestionsResponse>
} 