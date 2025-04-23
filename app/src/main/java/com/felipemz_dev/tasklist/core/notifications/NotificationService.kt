package com.felipemz_dev.tasklist.core.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.felipemz_dev.tasklist.R
import com.felipemz_dev.tasklist.core.notifications.NotificationScheduler.Companion.TAG_CHANEL_ID
import com.felipemz_dev.tasklist.core.utils.PreferencesUtils.Companion.removeScheduledNotification
import com.felipemz_dev.tasklist.ui.view.MainActivity

class NotificationService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (intent.hasExtra(NotificationScheduler.NOTIFICATION_DATA)) {
                val notificationTask =
                    intent.getSerializableExtra(NotificationScheduler.NOTIFICATION_DATA) as NotificationTask
                scheduleNotifications(notificationTask)
            }
        }
        return START_STICKY
    }

    private fun scheduleNotifications(notificationTask: NotificationTask) {

        //Intent to change task to done
        val intentCheckDone = Intent(this, MainActivity::class.java)
        intentCheckDone.action = MainActivity.ACTION_CHECK_DONE
        intentCheckDone.putExtra(MainActivity.TAG_GET_TASK, notificationTask.id)
        val pendingIntentCheckDone = PendingIntent.getActivity(
            this, 0, intentCheckDone,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        //Intent to edit task
        val intentEdit = Intent(this, MainActivity::class.java)
        intentEdit.action = MainActivity.ACTION_EDIT_TASK
        intentEdit.putExtra(MainActivity.TAG_GET_TASK, notificationTask.id)
        val pendingIntentEdit = PendingIntent.getActivity(
            this, 0, intentEdit,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        //Intent to delete task
        val intentEditDelete = Intent(this, MainActivity::class.java)
        intentEditDelete.action = MainActivity.ACTION_DELETE_TASK
        intentEditDelete.putExtra(MainActivity.TAG_GET_TASK, notificationTask.id)
        val pendingIntentDelete = PendingIntent.getActivity(
            this, 0, intentEditDelete,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val soundUri = Uri.parse("android.resource://$packageName/" + R.raw.ringtone)
        val titleNotification = notificationTask.title.ifEmpty { getString(R.string.task_reminder) }
        val isVersionGreaterOreo = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        //create notification
        val notification = NotificationCompat.Builder(this).apply {
            if (isVersionGreaterOreo){
                setChannelId(TAG_CHANEL_ID)
                setLargeIcon(Icon.createWithResource(this@NotificationService, R.mipmap.ic_launcher))
            } else setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
        }.setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSmallIcon(R.drawable.ic_notifications)
            .setColor(resources.getColor(R.color.apricot))
            .setContentTitle(titleNotification)
            .setContentText(notificationTask.text)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(notificationTask.bigText)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntentEdit)
            .addAction(
                androidx.appcompat.R.drawable.abc_ic_clear_material,
                getString(R.string.delete_task),
                pendingIntentDelete
            )
            .addAction(
                androidx.appcompat.R.drawable.btn_checkbox_checked_mtrl,
                getString(R.string.have_you_already_done_it),
                pendingIntentCheckDone
            )
            .setColorized(true)
            .setVibrate(longArrayOf(100, 200, 0, 200, 300, 0, 300, 400))

        //send notification
        notificationManager.notify(notificationTask.id.toInt(), notification.build())
        if (isVersionGreaterOreo){
            startForeground(1, notification.build())
            stopSelf()
        }
        removeScheduledNotification(this, notificationTask)
        turnOnScreen()
    }

    private fun turnOnScreen() {
        val powerManager = this.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "TaskList:Wakelock"
        )
        wakeLock.acquire(10 * 60 * 1000L)
        wakeLock.release()
    }
}