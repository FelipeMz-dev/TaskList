package com.felipemz_dev.tasklist.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipemz_dev.tasklist.core.AlarmHelper
import com.felipemz_dev.tasklist.core.toNotificationTask
import com.felipemz_dev.tasklist.data.TaskRepository
import com.felipemz_dev.tasklist.data.local.TaskEntity
import kotlinx.coroutines.launch

class EditTaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _task = MutableLiveData<TaskEntity>()
    val task: LiveData<TaskEntity> get() = _task
    val remember = MutableLiveData(true)

    private var alarmHelper: AlarmHelper? = null
    fun setAlarmHelper(alarmHelper: AlarmHelper) {
        this.alarmHelper = alarmHelper
    }

    fun getItemData(id: Long) {
        viewModelScope.launch {
            val value = repository.getItemData(id)
            _task.value = value
        }
    }

    fun saveData(text: String, expiryDate: String, listSteps: String) {
        if (task.value == null){
            val newTask = TaskEntity(0, text, expiryDate, remember.value == true,listSteps)
            insertData(newTask)
        } else {
            val newTask = task.value!!.copy(
                taskText = text,
                expiryDate = expiryDate,
                isRemember = remember.value == true,
                listSteps = listSteps
            )
            updateData(newTask)
            if (remember.value == true) {
                newTask.toNotificationTask("")?.let {
                    alarmHelper?.cancelNotification(it)
                    alarmHelper?.scheduleNotification(it)
                }
            }
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