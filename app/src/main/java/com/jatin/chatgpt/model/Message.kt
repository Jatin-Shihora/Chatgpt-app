package com.jatin.chatgpt.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.jatin.chatgpt.model.enums.MessageStatus

/**
 * @author Jatin
 * @time 30/04/2023
 */
@Entity(tableName = "message")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @Expose @SerializedName("role") val role: String,
    @Expose @SerializedName("content") val content: String,
    val sessionId: Int = 0,
    //Response time
    val responseTime: Long = 1,
    //insert time
    val insertTime: Long = System.currentTimeMillis(),
    var status: Int = MessageStatus.UNFINISHED.status
) {

    fun toDTO(): MessageDTO {
        return MessageDTO(role = role, content = content)
    }
}
