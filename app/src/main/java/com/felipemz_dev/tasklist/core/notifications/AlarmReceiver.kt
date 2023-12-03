package com.felipemz_dev.tasklist.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.felipemz_dev.tasklist.core.notifications.NotificationScheduler.Companion.NOTIFICATION_DATA

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra(NOTIFICATION_DATA)) {
            val notificationTask =
                intent.getSerializableExtra(NOTIFICATION_DATA) as NotificationTask
            val service = Intent(context, NotificationService::class.java)
            service.putExtra(NOTIFICATION_DATA, notificationTask)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service)
            } else {
                context.startService(service)
            }
        }
    }
}
