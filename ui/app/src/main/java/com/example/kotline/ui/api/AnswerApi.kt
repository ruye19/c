package com.example.kotline.ui.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Data class for an answer
// Matches backend: answerid, userid, questionid, answer, username

data class Answer(
    val answerid: String?,
    val userid: String?,
    val questionid: String?,
    val answer: String,
    val username: String?
)

data class AnswersResponse(
    val message: String,
    val answers: List<Answer>
)

data class PostAnswerRequest(
    val questionid: String,
    val answer: String,
    val userid: String
)

data class PostAnswerResponse(
    val message: String
)

interface AnswerApi {
    @GET("answers/{questionid}")
    suspend fun getAnswers(@Path("questionid") questionid: String): Response<AnswersResponse>

    @POST("answers")
    suspend fun postAnswer(@Body request: PostAnswerRequest): Response<PostAnswerResponse>
} 