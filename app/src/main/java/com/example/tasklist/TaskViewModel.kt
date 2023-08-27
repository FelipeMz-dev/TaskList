package com.example.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasklist.data.entities.TaskEntity
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    fun insertData(data: TaskEntity) {
        viewModelScope.launch {
            repository.insertData(data)
        }
    }

    fun updateData(data: TaskEntity) {
        viewModelScope.launch {
            repository.updateData(data)
        }
    }

    fun deleteData(data: TaskEntity) {
        viewModelScope.launch {
            repository.deleteData(data)
        }
    }

    fun getAllData() = repository.getAllData()

    suspend fun getItemData(id: Long): TaskEntity? {
        return withContext(viewModelScope.coroutineContext) {
            repository.getItemData(id)
        }
    }
}