package com.felipemz_dev.tasklist.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipemz_dev.tasklist.core.notifications.NotificationScheduler
import com.felipemz_dev.tasklist.core.notifications.toNotificationTask
import com.felipemz_dev.tasklist.data.TaskRepository
import com.felipemz_dev.tasklist.data.local.TaskEntity
import kotlinx.coroutines.launch

class MainViewModel(private val repository: TaskRepository) : ViewModel() {

    private val taskList = MutableLiveData<List<TaskEntity>>()
    private val _filteredTask = MutableLiveData<List<TaskEntity>>()
    val filteredTask: LiveData<List<TaskEntity>> get() = _filteredTask
    val isLoading = MutableLiveData(true)
    val isEmpty = MutableLiveData(false)

    private var alarmHelper: NotificationScheduler? = null
    fun setAlarmHelper(alarmHelper: NotificationScheduler) {
        this.alarmHelper = alarmHelper
    }

    init {
        var flagInit = false
        viewModelScope.launch {
            repository.getAllData().observeForever { newList ->
                if (flagInit) {
                    val task = taskList.value?.let { findNewTask(newList, it) }
                    if (task != null) {
                        if (task.isRemember) {
                            task.toNotificationTask("").let {
                                alarmHelper?.scheduleNotification(it)
                                alarmHelper?.requestEnableNotificationChanel()
                            }
                        }
                    }
                }
                taskList.value = newList
                cancelLoading()
                flagInit = true
            }
        }
    }

    private fun cancelLoading(){
        if (isLoading.value == true) isLoading.value = false
    }

    private fun findNewTask(list1: List<TaskEntity>, list2: List<TaskEntity>): TaskEntity? {
        if (list1.size <= list2.size) return null
        val difference = list1.subtract(list2.toSet())
        return if (difference.isNotEmpty()) {
            difference.first()
        } else {
            null
        }
    }

    fun filterAllData() {
        viewModelScope.launch {
            taskList.observeForever {
                _filteredTask.value = it
                isEmpty.value = it.isEmpty()
            }
        }
    }

    fun filterTodo() {
        viewModelScope.launch {
            taskList.observeForever { taskList ->
                _filteredTask.value = taskList.filter { !it.done }
            }
        }
    }

    fun filterDone() {
        viewModelScope.launch {
            taskList.observeForever { taskList ->
                _filteredTask.value = taskList.filter { it.done }
            }
        }
    }

    fun updateData(data: TaskEntity) {
        val newData = data.copy(done = !data.done)
        viewModelScope.launch {
            repository.updateData(newData)
        }
    }

    fun deleteData(data: TaskEntity) {
        viewModelScope.launch {
            repository.deleteData(data)
            if (data.isRemember) {
                data.toNotificationTask("").let { alarmHelper?.cancelNotification(it) }
            }
        }
    }

    fun getItemData(id: Long): TaskEntity? {
        return taskList.value?.find { it.id == id }
    }
}