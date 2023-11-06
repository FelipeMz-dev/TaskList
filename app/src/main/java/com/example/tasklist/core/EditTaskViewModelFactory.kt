package com.example.tasklist.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tasklist.data.TaskRepository
import com.example.tasklist.ui.viewmodel.EditTaskViewModel

class EditTaskViewModelFactory(
    private val repository: TaskRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditTaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditTaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}