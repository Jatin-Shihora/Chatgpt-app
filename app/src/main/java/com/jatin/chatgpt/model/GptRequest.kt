package com.jatin.chatgpt.model

import com.google.gson.annotations.SerializedName


/**
 * request body
 *
 * @author Jatin
 * @time 30/04/2023
 */
data class GptRequest(
    @SerializedName("messages") val messages: List<MessageDTO>,
    @SerializedName("model") val model: String,
)

