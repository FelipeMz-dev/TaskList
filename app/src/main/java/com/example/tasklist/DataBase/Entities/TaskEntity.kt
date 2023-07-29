package com.example.tasklist.DataBase.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "taskText") var taskText: String,
    @ColumnInfo(name = "expiryDate") var expiryDate: String,
    @ColumnInfo(name = "ListSteps") var listSteps: String,
    @ColumnInfo(name = "done") var done: Boolean = false)