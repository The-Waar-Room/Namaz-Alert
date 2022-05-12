package com.sudoajay.namaz_alert.ui.notification

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.sudoajay.namaz_alert.R

/**
 * Static class containing IDs of notification channels and code to create them.
 */
object NotificationChannels {
    private const val GROUP_ALERT = "com.sudoajay.namaz_alert.notifications.alert"
    const val ALERT_PRAYER_TIME = "com.sudoajay.namaz_alert.notifications.alert.prayer_time"


    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic
    fun notificationOnCreate(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannelGroup(
            NotificationChannelGroup(
                GROUP_ALERT,
                context.getString(R.string.notification_alert_services)
            )
        )


        val speedRunningChannel = NotificationChannel(
            ALERT_PRAYER_TIME,
            context.getString(R.string.notifications_alert_prayer_time),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        speedRunningChannel.setSound(null , null)
        speedRunningChannel.description = context.getString(R.string.notifications_running_desc)
        speedRunningChannel.group = GROUP_ALERT
        speedRunningChannel.setShowBadge(false)
        notificationManager.createNotificationChannel(speedRunningChannel)




    }
}