package com.jatin.chatgpt.repository.local

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * dao base class
 *
 * @author Jatin
 * @time 30/04/2023
 */
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(t: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<T>)

    @Update
    fun update(entity: T)

    @Delete
    fun delete(entity: T)

}