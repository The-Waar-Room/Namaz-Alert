package com.sudoajay.namaz_alert.ui.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.AudioManager
import androidx.work.WorkManager
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.ui.BaseActivity.Companion.notificationCancelReceiver
import com.sudoajay.namaz_alert.ui.BaseActivity.Companion.receiverId
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.prayerNameID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.prayerTimeID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.previousModeID
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
import com.sudoajay.namaz_alert.util.Helper


class NotificationCancelReceiver : BroadcastReceiver() {
    private lateinit var notificationBuilder: Notification.Builder

    lateinit var alertNotification: AlertNotification
    lateinit var mContext: Context

    override fun onReceive(context: Context?, intent: Intent?) {

        alertNotification = context?.let { AlertNotification(it) }!!
        mContext = context


        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(AlertNotification.NOTIFICATION_ALERT_STATE)

        manager.cancel(AlertNotification.NOTIFICATION_FinishCancel_STATE)

        WorkManager.getInstance(context).cancelAllWorkByTag(WorkMangerForTask.alertTAGID)

        val prayerName = intent!!.getStringExtra(prayerNameID).toString()
        startNotification(
            context.getString(
                R.string.cancel_the_notification_prayer, prayerName
            ),
            context.getString(R.string.click_here_to_setup_text),
            prayerName,
            intent.getStringExtra(
                prayerTimeID
            ).toString()
        )

        Helper.setWorkMangerCancel(null,context,true)


        val previousMode = intent.getStringExtra(previousModeID).toString()
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.ringerMode = Helper.getPhoneMode(previousMode)

    }

    private fun startNotification(
        title: String,
        subTitle: String,
        prayerName: String,
        prayerTime: String
    ) {
        createNotification(prayerName, prayerTime)
        alertNotification.notifyBuilder(
            title, subTitle, notificationBuilder
        )
    }

    private fun createNotification(
        prayerName: String,
        prayerTime: String
    ) {
        notificationBuilder =
            Notification.Builder(mContext, NotificationChannels.FINISH_CANCEL_PRAYER)
        notificationBuilder.setSmallIcon(R.drawable.ic_more_app)

        notificationBuilder.setContentIntent(createPendingIntent(prayerName, prayerTime))
    }

    private fun createPendingIntent(
        prayerName: String,
        prayerTime: String
    ): PendingIntent? {

        val intent = Intent(mContext, MainActivity::class.java)
        intent.putExtra(prayerNameID, prayerName)
        intent.putExtra(prayerTimeID, prayerTime)
        intent.putExtra(receiverId, notificationCancelReceiver)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        return PendingIntent.getActivity(
            mContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}