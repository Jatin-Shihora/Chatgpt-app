package com.jatin.chatgpt.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

/**
 * @author Jatin
 * @time 30/04/2023
 */
@Entity
data class MessageDTO(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String,
){
}
