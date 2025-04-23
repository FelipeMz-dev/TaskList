package com.felipemz_dev.tasklist.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felipemz_dev.tasklist.core.PikerImageLoader
import com.felipemz_dev.tasklist.core.extensions.makeCustomDatePicker
import com.felipemz_dev.tasklist.core.extensions.makeCustomTimePicker
import com.felipemz_dev.tasklist.core.notifications.NotificationScheduler
import com.felipemz_dev.tasklist.core.notifications.toNotificationTask
import com.felipemz_dev.tasklist.core.utils.ImageUtils
import com.felipemz_dev.tasklist.core.utils.TextDateUtils
import com.felipemz_dev.tasklist.data.TaskRepository
import com.felipemz_dev.tasklist.data.local.TaskEntity
import com.felipemz_dev.tasklist.ui.recyclerView.adapter.TaskNoteAdapter
import com.felipemz_dev.tasklist.ui.view.EditTaskActivity
import kotlinx.coroutines.launch
import java.util.Calendar

class EditTaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _task = MutableLiveData<TaskEntity>()
    val task: LiveData<TaskEntity> get() = _task
    val remember = MutableLiveData(false)

    private val imagePositionSelected = MutableLiveData(null as Int?)

    private var _listNote = MutableLiveData(emptyList<String>())
    val listNote: LiveData<List<String>> get() = _listNote

    val dateText = MutableLiveData("")
    val timeText = MutableLiveData("")

    private var alarmHelper: NotificationScheduler? = null
    fun setAlarmHelper(alarmHelper: NotificationScheduler) {
        this.alarmHelper = alarmHelper
    }

    fun getItemData(id: Long) {
        viewModelScope.launch {
            val value = repository.getItemData(id)
            _task.value = value?: TaskEntity()
            _listNote.value = value?.listSteps?.takeIf {
                it.isNotEmpty()
            }?.split(EditTaskActivity.TAG_DELIMITER) ?: emptyList()
            remember.value = value?.isRemember
        }
    }

    fun saveData(
        text: String,
        color: Int
    ): Boolean {
        val expiryDate = "${dateText.value} ${timeText.value}"
        val taskText = text.trim().ifEmpty { return false }
        val newListNote = _listNote.value?.joinToString(EditTaskActivity.TAG_DELIMITER)
        val newExpiryDate = if (remember.value == true) expiryDate else ""
        if (task.value == null) {
            val newTask = TaskEntity(
                taskText = taskText,
                expiryDate = newExpiryDate,
                isRemember = remember.value == true,
                listSteps = newListNote.orEmpty(),
                color = color
            )
            insertData(newTask)
        } else {
            val newTask = task.value!!.copy(
                taskText = taskText,
                expiryDate = expiryDate,
                isRemember = remember.value == true,
                listSteps = newListNote.orEmpty(),
                color = color
            )
            updateData(newTask)
        }
        return true
    }

    private fun insertData(data: TaskEntity) {
        viewModelScope.launch {
            repository.insertData(data)
        }
    }

    private fun updateData(data: TaskEntity) {
        viewModelScope.launch {
            updateNotification(data)
            repository.updateData(data)
        }
    }

    private fun updateNotification(data: TaskEntity) {
        if (data.done) return
        if (data.isRemember) {
            data.toNotificationTask("").let {
                if (!TextDateUtils.isDateExpired(dateText.value!!, timeText.value!!)) {
                    alarmHelper?.scheduleNotification(it)
                    alarmHelper?.requestEnableNotificationChanel()
                } else {
                    task.value?.toNotificationTask("")?.let { oldTask ->
                        if (!TextDateUtils.isDateExpired(oldTask.timeNotification)) {
                            alarmHelper?.cancelNotification(it)
                        }
                    }
                }
            }
        } else {
            task.value?.toNotificationTask("")?.let { oldTask ->
                if (!TextDateUtils.isDateExpired(oldTask.timeNotification)) {
                    data.toNotificationTask("").let { alarmHelper?.cancelNotification(it) }
                }
            }
        }
    }

    fun addNote(text: String) {
        if (text.trim().isNotEmpty()) {
            _listNote.value = _listNote.value?.let {
                it.toMutableList().apply { add(text) }
            }
        }
    }

    fun editNote(
        text: String,
        position: Int
    ) {
        if (text.trim().isNotEmpty()) {
            _listNote.value = _listNote.value?.let {
                it.toMutableList().apply { set(position, text) }
            }
        }
    }

    private fun addNoteImage(image: Bitmap) {
        val imagePath = ImageUtils.saveBitmapToDirectory(image)
        if (imagePath != null) {
            val text = "${EditTaskActivity.TAG_IS_IMAGE_NOTE}$imagePath"
            addNote(text)
        }
    }

    private fun editNoteImage(
        image: Bitmap,
        position: Int
    ) {
        val imagePath = ImageUtils.saveBitmapToDirectory(image)
        if (imagePath != null) {
            val text = "${EditTaskActivity.TAG_IS_IMAGE_NOTE}$imagePath"
            editNote(text, position)
        }
    }

    fun removeNote(viewHolder: TaskNoteAdapter.TaskStepsViewHolder) {
        val position = viewHolder.adapterPosition
        _listNote.value = _listNote.value?.let {
            it.toMutableList().apply { removeAt(position) }
        }
    }

    fun openImageChooserIntent(
        activity: Activity,
        position: Int? = null
    ) {
        imagePositionSelected.value = position
        PikerImageLoader.showPicker(activity) {
            activity.startActivityForResult(it, EditTaskActivity.CODE_SELECT_IMAGE)
        }
    }

    fun setNewImageToNote(imageBitmap: Bitmap) {
        val position = imagePositionSelected.value
        if (position != null) {
            editNoteImage(imageBitmap, position)
        } else addNoteImage(imageBitmap)
    }

    fun showDatePicker(context: Context) {
        dateText.value?.let { text ->
            Calendar.getInstance().makeCustomDatePicker(context, text) {
                dateText.value = TextDateUtils.loadDateOnTextView(it)
            }.show()
        }
    }

    fun showTimePicker(context: Context) {
        timeText.value?.let { text ->
            Calendar.getInstance().makeCustomTimePicker(context, text) {
                timeText.value = TextDateUtils.loadTimeOnTextView(it)
            }.show()
        }
    }

}