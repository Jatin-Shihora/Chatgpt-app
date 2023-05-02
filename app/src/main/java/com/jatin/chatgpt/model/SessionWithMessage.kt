package com.jatin.chatgpt.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * @author Jatin
 * @time 30/04/2023
 */
data class SessionWithMessage(
    @Embedded
    val session: Session,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val messages: List<Message>
) {

}