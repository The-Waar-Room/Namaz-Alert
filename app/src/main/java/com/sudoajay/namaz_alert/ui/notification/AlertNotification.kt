package com.sudoajay.namaz_alert.ui.notification


import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.sudoajay.namaz_alert.R

import com.sudoajay.namaz_alert.ui.notification.NotificationServices.Companion.NOTIFICATION_ALERT_SETTING
import com.sudoajay.namaz_alert.ui.notification.NotificationServices.Companion.NOTIFICATION_ALERT_STATE
import com.sudoajay.namaz_alert.ui.notification.NotificationServices.Companion.NOTIFICATION_ALERT_STOP
import com.sudoajay.namaz_alert.ui.notification.NotificationServices.Companion.commandNotification
import com.sudoajay.namaz_alert.ui.notification.NotificationServices.Companion.openSetting
import com.sudoajay.namaz_alert.ui.notification.NotificationServices.Companion.stopAlert
import com.sudoajay.namaz_alert.util.Command

import javax.inject.Inject


class AlertNotification @Inject constructor (var context: Context) {
    var notificationManager: NotificationManager? = null
    var notification: Notification? = null

    fun notifyBuilder(prayerName: String,prayerTime :String,phoneMode :String,beforeTime :Int , afterTime:Int ,
                      builder: Notification.Builder) { // local variable



        // now check for null notification manger
        if (notificationManager == null) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        builder

            .setContentTitle(context.getString(R.string.notification_title_text))
            .setContentText( context.getString(R.string.notification_context_title_text, prayerName) )
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(true)



        val iStyle =
            Notification.InboxStyle()
        iStyle.addLine(prayerName)
        iStyle.addLine(prayerTime)
        iStyle.addLine("\n")
        iStyle.addLine(context.getString(R.string.notification_sub_title_text ,phoneMode ))
        iStyle.addLine("$beforeTime  -  $afterTime")
        builder.style = iStyle

        // check if there ia data with empty
// more and view button classification
         notification = builder.build()

        notification!!.flags =
            notification!!.flags or (Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT)

        notifyNotification(notification!!)
    }

    fun notifyCompat(
        prayerName: String,prayerTime :String,phoneMode :String,beforeTime :String , afterTime:String,
        builder: NotificationCompat.Builder
    ) { // local variable

//        Pending Intent For Setting Action
        val settingPendingIntent = PendingIntent.getService(
            context, NOTIFICATION_ALERT_SETTING, Intent(context, NotificationServices::class.java)
                .putExtra("COMMAND", Command.Setting.ordinal), FLAG_UPDATE_CURRENT
        )

//        Pending Intent For Stop Action
        val stopPendingIntent = PendingIntent.getService(
            context, NOTIFICATION_ALERT_STOP, Intent(context, NotificationServices::class.java)
                .putExtra("COMMAND", Command.STOP.ordinal), FLAG_UPDATE_CURRENT
        )

        // now check for null notification manger
        if (notificationManager == null) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        // Default ringtone
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        builder

            .addAction(
                R.drawable.ic_setting,
                context.getString(R.string.notification_setting_text),
                settingPendingIntent
            )

            .addAction(
                R.drawable.ic_stop, context.getString(R.string.notification_stop_text),
                stopPendingIntent
            )

            // Set appropriate defaults for the notification light, sound,
            // and vibration.
            .setDefaults(Notification.DEFAULT_ALL) // Set required fields, including the small icon, the
            .setContentTitle(context.getString(R.string.notification_title_text))
            .setContentText( context.getString(R.string.notification_context_title_text,prayerName) )

            .setOngoing(true)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSound(uri) // Provide a large icon, shown with the notification in the
            .color = ContextCompat.getColor(context, R.color.appTheme)
        // If this notification relates to a past or upcoming event, you

        //Content hen expanded


        val iStyle =
            NotificationCompat.InboxStyle()
        iStyle.addLine(prayerName)
        iStyle.addLine(prayerTime)
        iStyle.addLine("\n")
        iStyle.addLine(context.getString(R.string.notification_sub_title_text ,phoneMode ))
        iStyle.addLine("$beforeTime  -  $afterTime")
        builder.setStyle(iStyle)

        // check if there ia data with empty
// more and view button classification
        notification = builder.build()

        notification!!.flags =
            notification!!.flags or (Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT)

        notifyNotification(notification!!)
    }

     fun notifyNotification(notification: Notification) {
        notificationManager!!.notify(NOTIFICATION_ALERT_STATE, notification)

    }



}