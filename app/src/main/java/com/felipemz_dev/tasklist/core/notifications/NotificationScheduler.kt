package com.felipemz_dev.tasklist.core.notifications

import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.felipemz_dev.tasklist.R
import com.felipemz_dev.tasklist.core.extensions.makeResourcesToast
import com.felipemz_dev.tasklist.core.utils.PreferencesUtils.Companion.removeScheduledNotification
import com.felipemz_dev.tasklist.core.utils.PreferencesUtils.Companion.saveScheduledNotification


class NotificationScheduler(private val context: Context) {

    companion object {
        const val TAG_CHANEL_ID = "TaskRememberChanelId"
        const val NOTIFICATION_DATA = "NotificationData"
    }

    init {
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(TAG_CHANEL_ID) == null) {
                val channel = NotificationChannel(
                    TAG_CHANEL_ID,
                    context.getString(R.string.task_todo),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                    description = context.getString(R.string.remember_your_tasks)
                    enableVibration(true)
                    enableLights(true)
                    lightColor = context.getColor(R.color.apricot)
                    vibrationPattern = longArrayOf(100, 200, 0, 200, 300, 0, 300, 400)
                    setSound(
                        Uri.parse("android.resource://${context.packageName}/" + R.raw.ringtone),
                        AudioAttributes.Builder().build()
                    )
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    fun requestEnableNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = manager.getNotificationChannel(TAG_CHANEL_ID)
            if (channel?.importance == NotificationManager.IMPORTANCE_NONE) {
                showEnableNotificationsDialog()
            }
        }
        val notificationManager = NotificationManagerCompat.from(context)
        if (!notificationManager.areNotificationsEnabled()) {
            showEnableNotificationsDialog()
        }
    }

    private fun openAppNotificationSettings() {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else {
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)
        }
        context.startActivity(intent)
    }

    private fun showEnableNotificationsDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.enable_notifications))
            .setMessage(context.getString(R.string.body_enable_notifications))
            .setPositiveButton(context.getString(R.string.str_enable)) { _, _ ->
                openAppNotificationSettings()
            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun scheduleNotification(notificationTask: NotificationTask) {
        val pendingIntent = getPendingIntent(notificationTask)
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTask.timeNotification, pendingIntent)
        saveScheduledNotification(context, notificationTask)
        (context as Activity).makeResourcesToast(R.string.schedule_notification)
    }

    fun cancelNotification(notificationTask: NotificationTask) {
        val manager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val pendingIntent = getPendingIntent(notificationTask)
        manager.cancel(pendingIntent)
        removeScheduledNotification(context, notificationTask)
        (context as Activity).makeResourcesToast(R.string.cancel_notification)
    }

    private fun getPendingIntent(notificationTask: NotificationTask): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(NOTIFICATION_DATA, notificationTask)

        return PendingIntent.getBroadcast(
            context.applicationContext,
            notificationTask.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}