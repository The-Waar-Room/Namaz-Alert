package com.sudoajay.namaz_alert.ui.notification


import android.app.Notification
import android.app.Notification.DEFAULT_ALL
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.model.Command
import com.sudoajay.namaz_alert.ui.BaseActivity
import com.sudoajay.namaz_alert.ui.BaseActivity.Companion.openMainActivityID
import com.sudoajay.namaz_alert.ui.BaseActivity.Companion.settingShortcutId
import com.sudoajay.namaz_alert.ui.background.AlarmsScheduler
import com.sudoajay.namaz_alert.ui.foreground.ForegroundService
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
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
            .setAutoCancel(false)



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
        timeDiff: String,
        previousMode :String ,notificationRingtone:Int,
        builder: NotificationCompat.Builder, intentString:String
    ) { // local variable

        val cancelIntent = Intent(context, ForegroundService::class.java)
        cancelIntent.putExtra(AlarmsScheduler.DATA_SHARE_ID,intentString)
        cancelIntent.putExtra(previousModeID, previousMode)
        cancelIntent.putExtra(BaseActivity.CommandTAG, Command.CANCEL.ordinal)


//        val cancelIntent = Intent(context,NotificationCancelReceiver::class.java)
//        cancelIntent.action = "com.sudoajay.namaz_alert.Action"
//        cancelIntent.putExtra(previousModeID, previousMode)
//        cancelIntent.putExtra(prayerNameID, prayerName)
//        cancelIntent.putExtra(prayerTimeID,prayerTime)

        val cancelPendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getService(context, NOTIFICATION_ALERT_STOP, cancelIntent,    PendingIntent.FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT )
            } else {
                PendingIntent.getService(context, NOTIFICATION_ALERT_STOP, cancelIntent,    FLAG_UPDATE_CURRENT )
            }

//        Pending Intent For Stop Action

        // now check for null notification manger
        if (notificationManager == null) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        Log.e("ALertNotification", "")
        // Default ringtone
        val uri = if (notificationRingtone == 0) null else Uri.parse("android.resource://"+context.packageName +"/"+R.raw.azan_in_islam)

        builder

            .addAction(
                R.drawable.ic_setting,
                context.getString(R.string.notification_setting_text),
                createPendingIntentSetting()
            )

            .addAction(
                R.drawable.ic_stop, context.getString(R.string.notification_stop_text),
                cancelPendingIntent
            )

            // Set appropriate defaults for the notification light, sound,
            // and vibration.
            .setDefaults(DEFAULT_ALL) // Set required fields, including the small icon, the
            .setContentTitle(context.getString(R.string.notification_title_text , prayerName))
            .setContentText(context.getString(R.string.notification_context_title_text, prayerTime))
            .setOngoing(true).setAutoCancel(false)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setLights(Color.RED, 3000, 3000)
            .setSound(uri) // Provide a large icon, shown with the notification in the
            .color = ContextCompat.getColor(context, R.color.appTheme)
        // If this notification relates to a past or upcoming event, you

        //Content hen expanded


        val iStyle =
            NotificationCompat.InboxStyle()
        iStyle.addLine(prayerTime)
        iStyle.addLine(context.getString(R.string.your_device_will_be_text, phoneMode.lowercase(),timeDiff ))
        builder.setStyle(iStyle)

        // check if there ia data with empty
// more and view button classification
        notification = builder.build()

        notification!!.flags = notification!!.flags or Notification.FLAG_AUTO_CANCEL

        notifyNotification(NOTIFICATION_ALERT_STATE, notification!!)

    }

    private fun notifyNotification(id:Int, notification: Notification) {
        notificationManager!!.notify(id, notification)

    }

    private fun createPendingIntentSetting(): PendingIntent? {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(openMainActivityID, settingShortcutId)
        return  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT )
        else PendingIntent.getActivity(
            context, NOTIFICATION_ALERT_SETTING, intent,
            FLAG_UPDATE_CURRENT
        )

    }

    companion object{
        const val NOTIFICATION_ALERT_STATE = 2
        const val NOTIFICATION_FinishCancel_STATE = 3
        const val NOTIFICATION_ALERT_SETTING = 4
        const val NOTIFICATION_ALERT_STOP = 5
        const val NOTIFICATION_ALERT_Resume = 6


        const val previousModeID= "PreviousModeID"
        const val notificationAlertID = "NotificationAlertID"
    }


}