package com.felipemz_dev.tasklist.core.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.felipemz_dev.tasklist.data.TaskRepository
import com.felipemz_dev.tasklist.ui.viewmodel.EditTaskViewModel

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