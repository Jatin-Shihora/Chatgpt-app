package com.jatin.chatgpt.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author Jatin
 * @time 30/04/2023
 */
@Entity(tableName = "session")
data class Session(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val lastSessionTime: Long
) {

}