package com.sudoajay.namaz_alert.ui.notification


import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.model.Command
import com.sudoajay.namaz_alert.ui.BaseActivity
import com.sudoajay.namaz_alert.ui.foreground.ForegroundService
import javax.inject.Inject


class UpComingNotification @Inject constructor(var context: Context) {
    var notificationManager: NotificationManager? = null
    var notification: Notification? = null

    fun notifyCompat(
        prayerName: String, prayerTime: String, builder: NotificationCompat.Builder
    ) { // local variable

        val stopIntent = Intent(context, ForegroundService::class.java)
        stopIntent.putExtra(BaseActivity.CommandTAG, Command.STOP.ordinal)


        val stopPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getService(
                context,
                AlertNotification.NOTIFICATION_ALERT_STOP,
                stopIntent,
                PendingIntent.FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getService(
                context, AlertNotification.NOTIFICATION_ALERT_STOP, stopIntent, FLAG_UPDATE_CURRENT
            )
        }
//        Pending Intent For Stop Action

        // now check for null notification manger
        if (notificationManager == null) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

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
            ).setOngoing(true).setAutoCancel(false)
            .setShowWhen(true)
            .setOnlyAlertOnce(true)
            .setWhen(System.currentTimeMillis())
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

        notifyNotification(notification!!)

    }


    fun notifyNotification(notification: Notification) {
        notificationManager!!.notify(NOTIFICATION_ID_STATE, notification)

    }

    companion object {
        const val NOTIFICATION_ID_STATE = 1
    }







}