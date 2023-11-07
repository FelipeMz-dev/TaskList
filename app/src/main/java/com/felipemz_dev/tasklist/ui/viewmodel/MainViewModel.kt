package com.felipemz_dev.tasklist.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipemz_dev.tasklist.data.TaskRepository
import com.felipemz_dev.tasklist.data.local.TaskEntity
import kotlinx.coroutines.launch

class MainViewModel(private val repository: TaskRepository) : ViewModel() {

    private val taskList = MutableLiveData<List<TaskEntity>>()
    private val _filteredTask = MutableLiveData<List<TaskEntity>>()
    val filteredTask: LiveData<List<TaskEntity>> get() = _filteredTask
    val isLoading = MutableLiveData(false)

    init {
        viewModelScope.launch {
            repository.getAllData().observeForever{ taskList.value = it }
        }
    }

    fun filterAllData() {
        viewModelScope.launch {
            taskList.observeForever {
                isLoading.value = true
                _filteredTask.value = it
                isLoading.value = false
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

    fun updateData(data: TaskEntity) {
        viewModelScope.launch { repository.updateData(data) }
    }

    fun deleteData(data: TaskEntity) {
        viewModelScope.launch { repository.deleteData(data) }
    }
}