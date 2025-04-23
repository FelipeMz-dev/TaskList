package com.felipemz_dev.tasklist.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.felipemz_dev.tasklist.data.local.dao.TaskDao

@Database(entities = [TaskEntity::class], version = 2)
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
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        if (database.version == 2) return
        database.execSQL("ALTER TABLE tasks ADD COLUMN color INTEGER NOT NULL DEFAULT 0")
    }
}