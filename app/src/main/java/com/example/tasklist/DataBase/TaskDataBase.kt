package com.example.tasklist.DataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tasklist.DataBase.Dao.TaskDao
import com.example.tasklist.DataBase.Entities.TaskEntity

@Database(entities = [TaskEntity::class], version = 1)
abstract class TaskDataBase : RoomDatabase(){
    abstract fun taskDao(): TaskDao
}