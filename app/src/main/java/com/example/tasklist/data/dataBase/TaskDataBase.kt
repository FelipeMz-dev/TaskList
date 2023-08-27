package com.example.tasklist.data.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tasklist.data.dataBase.dao.TaskDao
import com.example.tasklist.data.entities.TaskEntity

@Database(entities = [TaskEntity::class], version = 1)
abstract class TaskDataBase : RoomDatabase(){
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDataBase? = null

        fun getInstance(context: Context): TaskDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDataBase::class.java,
                    "task_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}