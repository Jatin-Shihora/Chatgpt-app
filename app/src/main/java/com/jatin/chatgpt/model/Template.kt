package com.jatin.chatgpt.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * template
 *
 * @author Jatin
 * @time 30/04/2023
 */
@Entity(tableName = "template")
data class Template(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tempContent: String = "",
    val name: String = "",
    val time: Long = System.currentTimeMillis()
) {
}