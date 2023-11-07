package com.felipemz_dev.tasklist.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipemz_dev.tasklist.data.TaskRepository
import com.felipemz_dev.tasklist.data.local.TaskEntity
import kotlinx.coroutines.launch

class EditTaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _task = MutableLiveData<TaskEntity>()
    val task: LiveData<TaskEntity> get() = _task

    fun getItemData(id: Long) {
        viewModelScope.launch {
            val value = repository.getItemData(id)
            _task.value = value
        }
    }

    fun saveData(text: String, expiryDate: String, listSteps: String) {
        if (task.value == null){
            val newTask = TaskEntity(0, text, expiryDate, listSteps)
            insertData(newTask)
        } else {
            val newTask = task.value!!.copy(
                taskText = text,
                expiryDate = expiryDate,
                listSteps = listSteps
            )
            updateData(newTask)
        }
    }

    private fun insertData(data: TaskEntity) {
        viewModelScope.launch {
            repository.insertData(data)
        }
    }

    private fun updateData(data: TaskEntity) {
        viewModelScope.launch {
            repository.updateData(data)
        }
    }
}