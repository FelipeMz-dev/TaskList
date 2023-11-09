package com.felipemz_dev.tasklist.core

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmHelper(private val context: Context) {

    companion object{
        const val TAG_CHANEL_ID = "MainActivityChanelId"
    }

    init {
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            if (manager.getNotificationChannel(TAG_CHANEL_ID) == null) {
                val channel = NotificationChannel(
                    TAG_CHANEL_ID,
                    "Task todo",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                manager.createNotificationChannel(channel)
            }
        }
    }

    fun scheduleNotification(notificationTask: NotificationTask){
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getPendingIntent(notificationTask)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTask.timeNotification, pendingIntent)
        } else {
            manager.setExact(AlarmManager.RTC_WAKEUP, notificationTask.timeNotification, pendingIntent)
        }
    }

    fun cancelNotification(notificationTask: NotificationTask){
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getPendingIntent(notificationTask)
        manager.cancel(pendingIntent)
    }

    private fun getPendingIntent(notificationTask: NotificationTask): PendingIntent {
        val intent = Intent(context.applicationContext, ScheduleNotificationManager::class.java)
        intent.putExtra("NotificationData", notificationTask)

        return PendingIntent.getBroadcast(
            context.applicationContext,
            notificationTask.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}