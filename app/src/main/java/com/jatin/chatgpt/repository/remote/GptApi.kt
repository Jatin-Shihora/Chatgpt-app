package com.jatin.chatgpt.repository.remote

import com.jatin.chatgpt.model.GptRequest
import com.jatin.chatgpt.model.GptResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers

import retrofit2.http.POST

/**
 * api
 *
 * @author Jatin
 * @time 30/04/2023
 */
interface GptApi {
    @POST("v1/chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun completions(
        @Header("Authorization") authKey: String, @Body requestBody: GptRequest
    ): GptResponse
}