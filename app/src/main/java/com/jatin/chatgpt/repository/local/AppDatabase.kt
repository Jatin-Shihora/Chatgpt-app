package com.jatin.chatgpt.repository.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jatin.chatgpt.model.Message
import com.jatin.chatgpt.model.Session
import com.jatin.chatgpt.model.Template
import com.jatin.template.common.Constants

@Database(
    entities = [Message::class, Session::class, Template::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getMessageDao(): MessageDao
    abstract fun getSessionDao(): SessionDao
    abstract fun getTemplateDao(): TemplateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDBInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    if (INSTANCE == null) {
                        INSTANCE = createRoomBuilder(context)
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }

        private fun createRoomBuilder(context: Context): Builder<AppDatabase> {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                Constants.DATABASE_NAME
            ).allowMainThreadQueries()
//                .addMigrations(MigrationDb1To2())
//                .addMigrations(MigrationDb2To3())
        }
    }
}