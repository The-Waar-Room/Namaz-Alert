package com.sudoajay.namaz_alert.ui.notification

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.sudoajay.namaz_alert.R


/**
 * Static class containing IDs of notification channels and code to create them.
 */
object NotificationChannels {
    private const val GROUP_NOTIFICATION =
        "com.sudoajay.namaz_alert.notifications.group_notification"
    private const val GROUP_SERVICE = "com.sudoajay.nblik.cloudmessaging.firebase_service"
    const val UPCOMING_PRAYER_TIME = "com.sudoajay.namaz_alert.notifications.upcoming.prayer_time"
    const val ALERT_DEFAULT_PRAYER_TIME =
        "com.sudoajay.namaz_alert.notifications.alert.default_prayer_time"
    const val ALERT_SOUND_PRAYER_TIME =
        "com.sudoajay.namaz_alert.notifications.alert.sound_prayer_time"

    const val FINISH_CANCEL_PRAYER = "com.sudoajay.namaz_alert.notifications.finish.cancel.prayer"
    const val FireBase_PUSH_NOTIFICATION =
        "com.sudoajay.nblik.cloudmessaging.firebase_push_notification"


    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic
    fun notificationOnCreate(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannelGroup(
            NotificationChannelGroup(
                GROUP_NOTIFICATION,
                context.getString(R.string.notification_main_services)
            )
        )


        val upComingPrayerChannel = NotificationChannel(
            UPCOMING_PRAYER_TIME,
            context.getString(R.string.notifications_upcoming_prayer_time),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        upComingPrayerChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, null)
        upComingPrayerChannel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
        upComingPrayerChannel.description = context.getString(R.string.notifications_upcoming_desc)
        upComingPrayerChannel.group = GROUP_NOTIFICATION
        upComingPrayerChannel.setShowBadge(false)
        upComingPrayerChannel.enableVibration(true)
        upComingPrayerChannel.enableLights(true)
        upComingPrayerChannel.lightColor = Color.RED
        notificationManager.createNotificationChannel(upComingPrayerChannel)


        val alertDefaultPrayerChannel = NotificationChannel(
            ALERT_DEFAULT_PRAYER_TIME,
            context.getString(R.string.notifications_alert_default_prayer_time),
            NotificationManager.IMPORTANCE_HIGH
        )

        alertDefaultPrayerChannel.setSound(
            Settings.System.DEFAULT_NOTIFICATION_URI,
            null
        )

        alertDefaultPrayerChannel.description = context.getString(R.string.notifications_alert_desc)
        alertDefaultPrayerChannel.group = GROUP_NOTIFICATION
        alertDefaultPrayerChannel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

        alertDefaultPrayerChannel.setShowBadge(false)
        alertDefaultPrayerChannel.enableVibration(true)
        alertDefaultPrayerChannel.enableLights(true)
        alertDefaultPrayerChannel.lightColor = Color.RED

        notificationManager.createNotificationChannel(alertDefaultPrayerChannel)


        val alertSoundPrayerChannel = NotificationChannel(
            ALERT_SOUND_PRAYER_TIME,
            context.getString(R.string.notifications_alert_sound_prayer_time),
            NotificationManager.IMPORTANCE_HIGH
        )
        val sound: Uri =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.azan_in_islam) //Here is FILE_NAME is the name of file that you want to play

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        alertSoundPrayerChannel.setSound(
            sound,
            attributes
        )

        alertSoundPrayerChannel.description = context.getString(R.string.notifications_alert_desc)
        alertSoundPrayerChannel.group = GROUP_NOTIFICATION
        alertSoundPrayerChannel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

        alertSoundPrayerChannel.setShowBadge(false)
        alertSoundPrayerChannel.enableVibration(true)
        alertSoundPrayerChannel.enableLights(true)
        alertSoundPrayerChannel.lightColor = Color.RED

        notificationManager.createNotificationChannel(alertSoundPrayerChannel)


        val finishCancelPrayerChannel = NotificationChannel(
            FINISH_CANCEL_PRAYER,
            context.getString(R.string.notifications_finish_prayer_time),
            NotificationManager.IMPORTANCE_LOW
        )
        finishCancelPrayerChannel.setSound(null, null)
        finishCancelPrayerChannel.description =
            context.getString(R.string.notifications_finish_desc)
        finishCancelPrayerChannel.group = GROUP_NOTIFICATION
        finishCancelPrayerChannel.setShowBadge(false)
        finishCancelPrayerChannel.enableVibration(false)
        finishCancelPrayerChannel.enableLights(false)
        notificationManager.createNotificationChannel(finishCancelPrayerChannel)


        val notificationManagerService =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManagerService.createNotificationChannelGroup(
            NotificationChannelGroup(
                GROUP_SERVICE,
                context.getString(R.string.notifications_group_service)
            )
        )

        val firebaseChannel = NotificationChannel(
            FireBase_PUSH_NOTIFICATION,
            context.getString(R.string.firebase_channel_id),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        firebaseChannel.description = context.getString(R.string.firebase_channel_id)
        firebaseChannel.group = GROUP_SERVICE
        firebaseChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, null)
        firebaseChannel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
        firebaseChannel.setShowBadge(false)
        firebaseChannel.enableVibration(true)
        firebaseChannel.enableLights(true)
        firebaseChannel.lightColor = Color.RED
        notificationManagerService.createNotificationChannel(firebaseChannel)


    }


}