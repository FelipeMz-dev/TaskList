package com.felipemz_dev.tasklist.core.utils

import android.content.Context
import android.content.SharedPreferences
import com.felipemz_dev.tasklist.core.notifications.NotificationTask

class PreferencesUtils {
    companion object{
        private const val USER_INIT_APP = "user_init_app"
        private const val USER_INIT_EDIT = "user_init_edit"
        private const val USER_INIT_MAIN = "user_init_main"

        fun isUserInitEdit(context: Context): Boolean {
            val preference = context.getSharedPreferences(USER_INIT_APP, 0)
            if (preference.getBoolean(USER_INIT_EDIT, true)) {
                preference.edit().putBoolean(USER_INIT_EDIT, false).apply()
                return true
            }
            return false
        }

        fun isUserInitMain(context: Context): Boolean {
            val preference = context.getSharedPreferences(USER_INIT_APP, 0)
            if (preference.getBoolean(USER_INIT_MAIN, true)) {
                preference.edit().putBoolean(USER_INIT_MAIN, false).apply()
                return true
            }
            return false
        }


        private const val NOTIFICATION_PREFERENCES = "notification_preferences"
        private const val NOTIFICATION_TEXT = "notificationText_"
        private const val NOTIFICATION_BIG_TEXT = "notificationBigText_"
        private const val NOTIFICATION_TIME = "notificationTime_"
        private const val NOTIFICATION_ID = "notificationId_"

        fun saveScheduledNotification(context: Context, notificationTask: NotificationTask) {
            val sharedPreferences = context.getSharedPreferences(NOTIFICATION_PREFERENCES, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putLong("$NOTIFICATION_ID${notificationTask.id}", notificationTask.id)
            editor.putString("$NOTIFICATION_TEXT${notificationTask.id}", notificationTask.text)
            editor.putString("$NOTIFICATION_BIG_TEXT${notificationTask.id}", notificationTask.bigText)
            editor.putLong("$NOTIFICATION_TIME${notificationTask.id}", notificationTask.timeNotification)
            editor.apply()
        }

        private fun getNotificationIdsFromPreferences(sharedPreferences: SharedPreferences): Set<Long> {
            return sharedPreferences.all.keys
                .filter { it.startsWith(NOTIFICATION_ID) }
                .map { it.removePrefix(NOTIFICATION_ID).toLong() }
                .toSet()
        }

        fun getScheduledNotifications(context: Context): List<NotificationTask> {
            val preferences = context.getSharedPreferences(NOTIFICATION_PREFERENCES, Context.MODE_PRIVATE)
            val notificationIds = getNotificationIdsFromPreferences(preferences)

            if (notificationIds.isEmpty()) {
                return emptyList()
            }

            val notificationList = mutableListOf<NotificationTask>()
            for ( id in notificationIds) {
                val notificationText = preferences.getString("$NOTIFICATION_TEXT$id", "")
                val notificationBigText = preferences.getString("$NOTIFICATION_BIG_TEXT$id", "")
                val notificationTime = preferences.getLong("$NOTIFICATION_TIME$id", 0)
                val notificationId = preferences.getLong("$NOTIFICATION_ID$id", 0)
                if (notificationText != null && notificationBigText != null && notificationTime != 0L) {
                    val notificationTask = NotificationTask(
                        id = notificationId,
                        text = notificationText,
                        bigText = notificationBigText,
                        timeNotification = notificationTime
                    )
                    notificationList.add(notificationTask)
                }
            }
            return notificationList
        }

        fun removeScheduledNotification(context: Context, notificationTask: NotificationTask) {
            val preferences = context.getSharedPreferences(NOTIFICATION_PREFERENCES, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.remove("$NOTIFICATION_ID${notificationTask.id}")
            editor.remove("$NOTIFICATION_TEXT${notificationTask.id}")
            editor.remove("$NOTIFICATION_BIG_TEXT${notificationTask.id}")
            editor.remove("$NOTIFICATION_TIME${notificationTask.id}")
            editor.apply()
        }
    }
}