package com.felipemz_dev.tasklist.core

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.felipemz_dev.tasklist.R
import com.felipemz_dev.tasklist.core.AlarmHelper.Companion.TAG_CHANEL_ID
import com.felipemz_dev.tasklist.ui.view.MainActivity

class ScheduleNotificationManager: BroadcastReceiver() {

    companion object{
        const val NOTIFICATION_DATA = "NotificationData"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            if (intent.hasExtra(NOTIFICATION_DATA)){
                val notificationTask = intent.getSerializableExtra(NOTIFICATION_DATA) as NotificationTask
                createSimpleNotification(context, notificationTask)
                //Toast.makeText(context, notificationTask.timeNotification.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createSimpleNotification(context: Context, notificationTask: NotificationTask) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val titleNotification = notificationTask.title.ifEmpty { context.resources.getString(R.string.task_reminder) }

        var notification = NotificationCompat.Builder(context, TAG_CHANEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(titleNotification)
            .setContentText(notificationTask.text)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(notificationTask.bigText)
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationTask.id, notification)
    }
}