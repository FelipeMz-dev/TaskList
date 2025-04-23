package com.felipemz_dev.tasklist.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.felipemz_dev.tasklist.core.notifications.NotificationScheduler.Companion.NOTIFICATION_DATA
import com.felipemz_dev.tasklist.core.utils.PreferencesUtils

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra(NOTIFICATION_DATA)) {
            val notificationTask = intent.getSerializableExtra(NOTIFICATION_DATA) as NotificationTask
            startNotification(context, notificationTask)
        }else {
            if (intent.action == Intent.ACTION_BOOT_COMPLETED) reprogramNotifications(context)
        }
    }

    private fun startNotification(context: Context, notificationTask: NotificationTask) {
        val service = Intent(context, NotificationService::class.java)
        service.putExtra(NOTIFICATION_DATA, notificationTask)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(service)
        } else {
            context.startService(service)
        }
    }

    private fun reprogramNotifications(context: Context){
        val scheduledNotifications = PreferencesUtils.getScheduledNotifications(context)
        for (notification in scheduledNotifications) {
            if (notification.timeNotification < System.currentTimeMillis()) {
                startNotification(context, notification)
            }else{
                NotificationScheduler(context).scheduleNotification(notification)
            }
        }
    }
}
