package com.example.tasklist.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasklist.data.TaskRepository
import com.example.tasklist.data.local.TaskEntity
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val taskList = MutableLiveData<List<TaskEntity>>()
    private val _filteredTask = MutableLiveData<List<TaskEntity>>()
    val filteredTask: LiveData<List<TaskEntity>> get() = _filteredTask

    init {
        viewModelScope.launch {
            repository.getAllData().observeForever{ taskList.value = it }
        }
    }

    fun filterAllData() {
        viewModelScope.launch {
            taskList.observeForever {
                _filteredTask.value = it
            }
        }
    }

    fun filterTodo() {
        viewModelScope.launch {
            taskList.observeForever {
                _filteredTask.value = it.filter {value -> !value.done }
            }
        }
    }

    fun filterDone() {
        viewModelScope.launch {
            taskList.observeForever {
                _filteredTask.value = it.filter {value -> value.done }
            }
        }
    }

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

    suspend fun getItemData(id: Long): TaskEntity? {
        return withContext(viewModelScope.coroutineContext) {
            repository.getItemData(id)
        }
    }


}