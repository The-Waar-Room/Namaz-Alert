package com.sudoajay.namaz_alert.ui.notification


import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.ui.background.AlarmsScheduler
import com.sudoajay.namaz_alert.ui.background.BroadcastAlarmReceiver
import com.sudoajay.namaz_alert.ui.notification.AlertNotification.Companion.NOTIFICATION_UPCOMING_STATE
import com.sudoajay.namaz_alert.ui.notification.AlertNotification.Companion.pendingIntentUpdateCurrentFlag
import javax.inject.Inject


class UpComingNotification @Inject constructor(var context: Context) {
    var notification: Notification? = null

    fun notifyCompat(
        prayerName: String,
        prayerTime: String,
        builder: NotificationCompat.Builder,
        notificationManager: NotificationManager
    ) { // local variable

        val stopIntent = Intent(context, BroadcastAlarmReceiver::class.java)
        stopIntent.action = AlarmsScheduler.ACTION_STOP


        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            AlertNotification.NOTIFICATION_ALERT_STOP,
            stopIntent,
            pendingIntentUpdateCurrentFlag()
        )
//        Pending Intent For Stop Action

        // now check for null notification manger


        Log.e("ALertNotification", "")
        // Default ringtone
        val uri = null
        builder

            .addAction(
                R.drawable.ic_stop,
                context.getString(R.string.notification_turn_off_text),
                stopPendingIntent
            )

            // Set appropriate defaults for the notification light, sound,
            // and vibration.
            .setDefaults(Notification.DEFAULT_ALL)

            .setContentTitle(context.getString(R.string.upcoming_notification_title_text))
            .setContentText(
                context.getString(
                    R.string.upcoming_notification_context_title_text, prayerName, prayerTime
                )
            ).setOngoing(false).setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setVibrate(longArrayOf(0L))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setLights(Color.RED, 3000, 3000)
            .setSound(uri) // Provide a large icon, shown with the notification in the
            .color = ContextCompat.getColor(context, R.color.appTheme)
        // If this notification relates to a past or upcoming event, you

        //Content hen expanded


        // check if there ia data with empty
// more and view button classification
        notification = builder.build()

        notification!!.flags = notification!!.flags or Notification.FLAG_AUTO_CANCEL

        notifyNotification(notificationManager, notification!!)

    }


    private fun notifyNotification(
        notificationManager: NotificationManager,
        notification: Notification
    ) {
        notificationManager.notify(NOTIFICATION_UPCOMING_STATE, notification)

    }


}