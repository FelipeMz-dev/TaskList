package com.felipemz_dev.tasklist.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.felipemz_dev.tasklist.data.local.dao.TaskDao
import com.felipemz_dev.tasklist.data.local.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllData(): LiveData<List<TaskEntity>> = taskDao.getAllTasks()

    @WorkerThread
    suspend fun getItemData(id: Long): TaskEntity? {
        return withContext(Dispatchers.IO){
            taskDao.getTaskById(id)
        }
    }

    @WorkerThread
    suspend fun insertData(data: TaskEntity) {
        withContext(Dispatchers.IO) {
            taskDao.insertTask(data)
        }
    }

    @WorkerThread
    suspend fun updateData(data: TaskEntity) {
        withContext(Dispatchers.IO) {
            taskDao.updateTask(data)
        }
    }

    @WorkerThread
    suspend fun deleteData(data: TaskEntity) {
        withContext(Dispatchers.IO) {
            taskDao.deleteTask(data)
        }
    }
}