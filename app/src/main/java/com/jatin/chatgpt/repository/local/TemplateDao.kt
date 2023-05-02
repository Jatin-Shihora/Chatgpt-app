package com.jatin.chatgpt.repository.local

import androidx.room.Dao
import androidx.room.Query
import com.jatin.chatgpt.model.Template

@Dao
interface TemplateDao : BaseDao<Template> {

    @Query("select * from template order by time desc")
    fun queryAll(): List<Template>

    @Query("update template set name=:name where id=:id")
    fun updateTemplateName(name: String, id: Int)
}