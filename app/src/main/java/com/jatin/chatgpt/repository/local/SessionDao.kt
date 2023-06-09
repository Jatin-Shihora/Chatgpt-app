package com.jatin.chatgpt.repository.local

import androidx.room.Dao
import androidx.room.Query
import com.jatin.chatgpt.model.Session

@Dao
interface SessionDao : BaseDao<Session> {

    @Query("select * from session order by lastSessionTime desc")
    fun queryAllSession(): List<Session>

    @Query(" select * from session order by lastSessionTime desc limit 1 ")
    fun queryLeastSession(): Session?

    @Query("update session set lastSessionTime=:lastSessionTime where id=:id")
    fun updateSessionTime(id: Int, lastSessionTime: Long)

    @Query("update session set title=:title where id=:id")
    fun updateSessionTitle(id: Int, title: String)

}