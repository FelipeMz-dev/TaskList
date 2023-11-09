package com.felipemz_dev.tasklist.core

import com.felipemz_dev.tasklist.data.local.TaskEntity
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class NotificationTask(
    val id: Int,
    val text: String = "",
    val title: String = "",
    val bigText: String = "",
    val timeNotification: Long = 0,
): Serializable

fun TaskEntity.toNotificationTask(title: String) =
    SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).parse(expiryDate)?.let {
        NotificationTask(
            id = id.toInt(),
            text = taskText.reduce(),
            title = title,
            bigText = taskText,
            timeNotification = it.time
        )
    }
