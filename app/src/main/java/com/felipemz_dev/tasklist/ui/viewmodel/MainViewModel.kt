package com.felipemz_dev.tasklist.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipemz_dev.tasklist.core.AlarmHelper
import com.felipemz_dev.tasklist.core.toNotificationTask
import com.felipemz_dev.tasklist.data.TaskRepository
import com.felipemz_dev.tasklist.data.local.TaskEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(private val repository: TaskRepository) : ViewModel() {

    private val taskList = MutableLiveData<List<TaskEntity>>()
    private val _filteredTask = MutableLiveData<List<TaskEntity>>()
    val filteredTask: LiveData<List<TaskEntity>> get() = _filteredTask
    val isLoading = MutableLiveData(false)
    val isEmpty = MutableLiveData(false)

    private var alarmHelper: AlarmHelper? = null
    fun setAlarmHelper(alarmHelper: AlarmHelper) {
        this.alarmHelper = alarmHelper
    }

    init {
        var flagInit = false
        viewModelScope.launch {
            repository.getAllData().observeForever{ newList ->
                if (flagInit) {
                    val task = taskList.value?.let { it1 -> findNewTask(newList, it1) }
                    if (task != null) {
                        if (task.isRemember){
                            task.toNotificationTask("")?.let {
                                alarmHelper?.scheduleNotification(it)
                            }
                        }
                    }
                }
                isLoading.value = true
                taskList.value = newList
                flagInit = true
            }
        }
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
                isLoading.value = false
                isEmpty.value = it.isEmpty()
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
        viewModelScope.launch {
            repository.deleteData(data)
            if (data.isRemember) {
                data.toNotificationTask("")?.let { alarmHelper?.cancelNotification(it) }
            }
        }
    }
}