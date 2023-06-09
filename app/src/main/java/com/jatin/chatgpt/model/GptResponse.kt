package com.jatin.chatgpt.model

import com.google.gson.annotations.SerializedName


/**
 * response body
 *
 * @author Jatin
 * @time 30/04/2023
 */
data class GptResponse(
    @SerializedName("choices") val choices: List<Choice>,
    @SerializedName("created") val created: Int,
    @SerializedName("id") val id: String,
    @SerializedName("model") val model: String,
    @SerializedName("object") val objectX: String,
    @SerializedName("usage") val usage: Usage,
    @SerializedName("error")
    val error: Error?

)

data class Error(
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("param")
    val `param`: Any,
    @SerializedName("type")
    val type: String
)

data class Choice(
    @SerializedName("finish_reason") val finishReason: String,
    @SerializedName("index") val index: Int,
    @SerializedName("message") val message: MessageDTO
)

data class Usage(
    @SerializedName("completion_tokens") val completionTokens: Int,
    @SerializedName("prompt_tokens") val promptTokens: Int,
    @SerializedName("total_tokens") val totalTokens: Int
)

