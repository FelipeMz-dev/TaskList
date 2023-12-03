package com.felipemz_dev.tasklist.core.notifications

import com.felipemz_dev.tasklist.core.extensions.reduce
import com.felipemz_dev.tasklist.data.local.TaskEntity
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class NotificationTask(
    val id: Long,
    val text: String = "",
    val title: String = "",
    val bigText: String = "",
    val timeNotification: Long = 0,
): Serializable

fun TaskEntity.toNotificationTask(title: String): NotificationTask {
    var time: Long = 0
    if (expiryDate.isNotEmpty()) {
        SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).parse(expiryDate)?.let {
            time = it.time
        }
    }
    return NotificationTask(
        id = id,
        text = taskText.reduce(),
        title = title,
        bigText = taskText,
        timeNotification = time
    )
}
