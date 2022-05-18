package com.sudoajay.namaz_alert.ui.notification

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.sudoajay.namaz_alert.R


/**
 * Static class containing IDs of notification channels and code to create them.
 */
object NotificationChannels {
    private const val GROUP_NOTIFICATION = "com.sudoajay.namaz_alert.notifications.group_notification"
    const val ALERT_PRAYER_TIME = "com.sudoajay.namaz_alert.notifications.alert.prayer_time"
    const val FINISH_CANCEL_PRAYER = "com.sudoajay.namaz_alert.notifications.finish.cancel.prayer"




    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic
    fun notificationOnCreate(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannelGroup(
            NotificationChannelGroup(
                GROUP_NOTIFICATION,
                context.getString(R.string.notification_group_services)
            )
        )


        val alertPrayerChannel = NotificationChannel(
            ALERT_PRAYER_TIME,
            context.getString(R.string.notifications_alert_prayer_time),
            NotificationManager.IMPORTANCE_HIGH
        )
        val sound: Uri =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.azan_in_islam) //Here is FILE_NAME is the name of file that you want to play


        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()
        alertPrayerChannel.setSound(sound , audioAttributes)
        alertPrayerChannel.description = context.getString(R.string.notifications_alert_desc)
        alertPrayerChannel.group = GROUP_NOTIFICATION
        alertPrayerChannel.enableVibration(true)
        alertPrayerChannel.enableLights(true)
        alertPrayerChannel.setShowBadge(false)

        notificationManager.createNotificationChannel(alertPrayerChannel)

        val finishCancelPrayerChannel = NotificationChannel(
            FINISH_CANCEL_PRAYER,
            context.getString(R.string.notifications_finish_prayer_time),
            NotificationManager.IMPORTANCE_LOW
        )
        finishCancelPrayerChannel.setSound(null , null)
        finishCancelPrayerChannel.description = context.getString(R.string.notifications_finish_desc)
        finishCancelPrayerChannel.group = GROUP_NOTIFICATION
        finishCancelPrayerChannel.setShowBadge(false)
        alertPrayerChannel.enableVibration(true)
        alertPrayerChannel.enableLights(true)
        notificationManager.createNotificationChannel(finishCancelPrayerChannel)



    }
}