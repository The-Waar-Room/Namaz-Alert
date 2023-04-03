package com.sudoajay.namaz_alert.ui.notification


import android.app.Notification
import android.app.Notification.DEFAULT_ALL
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.ui.BaseActivity.Companion.openMainActivityID
import com.sudoajay.namaz_alert.ui.BaseActivity.Companion.settingShortcutId
import com.sudoajay.namaz_alert.ui.background.AlarmsScheduler
import com.sudoajay.namaz_alert.ui.background.BroadcastAlarmReceiver
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
import com.sudoajay.namaz_alert.util.Helper
import javax.inject.Inject


class AlertNotification @Inject constructor(var context: Context) {
    var notification: Notification? = null

    fun notifyCompatCancelAndFinish(
        title: String,
        contentTitle: String,
        builder: NotificationCompat.Builder, notificationManager: NotificationManager
    ) { // local variable


        builder

            .setContentTitle(title) //"You Have Completed Your $prayerName namaz prayer"
            .setContentText(contentTitle) //Now your phone is set to previous mode : Normal mode
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(false)
            .setAutoCancel(true)


        // check if there ia data with empty
// more and view button classification
        notification = builder.build()

        notification!!.flags = notification!!.flags or Notification.FLAG_AUTO_CANCEL

        notifyNotification(notificationManager, NOTIFICATION_FinishCancel_STATE, notification!!)
    }

    fun notifyCompat(
        phoneMode: String,
        notificationRingtone: Int,
        builder: NotificationCompat.Builder,
        dataShare: String,
        notificationManager: NotificationManager
    ) { // local variable

        val data = dataShare.split("||")
        val prayerName = data[0]
        val prayerTime = Helper.convertTo12Hr(context, data[1])
        val timeDiff = data[2]

        val cancelIntent = Intent(context, BroadcastAlarmReceiver::class.java)
        cancelIntent.action = AlarmsScheduler.ACTION_CANCEL
        cancelIntent.putExtra(AlarmsScheduler.DATA_SHARE_ID, dataShare)


        val cancelPendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ALERT_STOP,
            cancelIntent,
            pendingIntentUpdateCurrentFlag()
        )


//        Pending Intent For Stop Action

        // now check for null notification manger


        Log.e("ALertNotification", "")
        // Default ringtone
        val uri =
            if (notificationRingtone == 0) Settings.System.DEFAULT_NOTIFICATION_URI else Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.azan_in_islam)

        builder

            .addAction(
                R.drawable.ic_setting,
                context.getString(R.string.notification_setting_text),
                createPendingIntentSetting()
            )

            .addAction(
                R.drawable.ic_stop, context.getString(R.string.notification_dismiss_text),
                cancelPendingIntent
            )

            // Set appropriate defaults for the notification light, sound,
            // and vibration.
            .setDefaults(DEFAULT_ALL) // Set required fields, including the small icon, the
            .setContentTitle(context.getString(R.string.notification_title_text, prayerName))
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
        iStyle.addLine(
            context.getString(
                R.string.your_device_will_be_text,
                phoneMode.lowercase(),
                timeDiff
            )
        )
        builder.setStyle(iStyle)

        // check if there ia data with empty
// more and view button classification
        notification = builder.build()

        notification!!.flags = notification!!.flags or Notification.FLAG_AUTO_CANCEL

        notifyNotification(notificationManager, NOTIFICATION_ALERT_STATE, notification!!)

    }

    private fun notifyNotification(
        notificationManager: NotificationManager,
        id: Int,
        notification: Notification
    ) {
        notificationManager.notify(id, notification)

    }

    private fun createPendingIntentSetting(): PendingIntent? {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(openMainActivityID, settingShortcutId)
        return PendingIntent.getActivity(
            context, 0, intent,
            pendingIntentUpdateCurrentFlag()
        )


    }


    companion object {
        const val NOTIFICATION_UPCOMING_STATE = 1
        const val NOTIFICATION_ALERT_STATE = 2
        const val NOTIFICATION_FinishCancel_STATE = 3
        const val NOTIFICATION_ALERT_SETTING = 4
        const val NOTIFICATION_ALERT_STOP = 5
        const val NOTIFICATION_ALERT_Resume = 6


        const val previousModeID = "PreviousModeID"
        const val notificationAlertID = "NotificationAlertID"

        fun pendingIntentUpdateCurrentFlag(): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                FLAG_UPDATE_CURRENT
            }
        }
    }


}