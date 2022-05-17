package com.sudoajay.namaz_alert.ui.notification


import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.icu.text.CaseMap
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.previousModeID
import com.sudoajay.namaz_alert.util.Command
import java.util.*
import javax.inject.Inject


class AlertNotification @Inject constructor(var context: Context) {
    var notificationManager: NotificationManager? = null
    var notification: Notification? = null

    fun notifyBuilder(
        title:String,
        contentTitle:String,
        builder: Notification.Builder
    ) { // local variable


        // now check for null notification manger
        if (notificationManager == null) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        builder

            .setContentTitle(title) //"You Have Completed Your $prayerName namaz prayer"
            .setContentText(contentTitle) //Now your phone is set to previous mode : Normal mode
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(false)



        // check if there ia data with empty
// more and view button classification
        notification = builder.build()

        notification!!.flags = notification!!.flags or Notification.FLAG_AUTO_CANCEL


        notifyNotification(NOTIFICATION_FinishCancel_STATE,notification!!)
    }

    fun notifyCompat(
        prayerName: String,
        prayerTime: String,
        phoneMode: String,
        beforeTime: String,
        afterTime: String,
        previousMode :String ,
        builder: NotificationCompat.Builder
    ) { // local variable

//        Pending Intent For Setting Action
        val settingPendingIntent = PendingIntent.getService(
            context, NOTIFICATION_ALERT_SETTING, Intent(context, NotificationServices::class.java)
                .putExtra("COMMAND", Command.Setting.ordinal), FLAG_UPDATE_CURRENT
        )

        val cancelIntent = Intent(context,NotificationCancelReceiver::class.java)
        cancelIntent.putExtra(previousModeID, previousMode)
        cancelIntent.putExtra(notificationAlertID , NOTIFICATION_ALERT_STATE )

        val cancelPendingIntent =
            PendingIntent.getBroadcast(context, NOTIFICATION_ALERT_STOP, cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT)

//        Pending Intent For Stop Action

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
                cancelPendingIntent
            )

            .addAction(
                R.drawable.ic_stop, context.getString(R.string.notification_stop_text),
                cancelPendingIntent
            )

            // Set appropriate defaults for the notification light, sound,
            // and vibration.
            .setDefaults(Notification.DEFAULT_ALL) // Set required fields, including the small icon, the
            .setContentTitle(context.getString(R.string.notification_title_text))
            .setContentText(context.getString(R.string.notification_context_title_text, prayerName))
            .setOngoing(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(uri) // Provide a large icon, shown with the notification in the
            .color = ContextCompat.getColor(context, R.color.appTheme)
        // If this notification relates to a past or upcoming event, you

        //Content hen expanded


        val iStyle =
            NotificationCompat.InboxStyle()
        iStyle.addLine(prayerName)
        iStyle.addLine(prayerTime)
        iStyle.addLine("\n")
        iStyle.addLine(context.getString(R.string.notification_sub_title_text, phoneMode))
        iStyle.addLine("$beforeTime  -  $afterTime")
        builder.setStyle(iStyle)

        // check if there ia data with empty
// more and view button classification
        notification = builder.build()

        notification!!.flags = notification!!.flags or Notification.FLAG_AUTO_CANCEL

        notifyNotification(NOTIFICATION_ALERT_STATE, notification!!)

    }

    fun notifyNotification(id:Int,notification: Notification) {
        notificationManager!!.notify(id, notification)

    }

    companion object{
        const val NOTIFICATION_ALERT_STATE = 1
        const val NOTIFICATION_ALERT_SETTING = 2
        const val NOTIFICATION_ALERT_STOP = 3
        const val NOTIFICATION_FinishCancel_STATE = 4


        const val notificationAlertID = "NotificationAlertID"
    }


}