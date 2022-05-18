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
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.prayerNameID
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
        WorkManager.getInstance(context).cancelAllWorkByTag(WorkMangerForTask.finishTAGID)


        startNotification(
            context.getString(
                R.string.cancel_the_notification_prayer, intent!!.getStringExtra(
                    prayerNameID
                ).toString()
            ), context.getString(R.string.click_here_to_setup_text)
        )

        val previousMode = intent.getStringExtra(previousModeID).toString()
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.ringerMode = Helper.getPhoneMode(previousMode)
    }

    private fun startNotification(title: String, subTitle: String) {
        createNotification()
        alertNotification.notifyBuilder(
            title, subTitle, notificationBuilder
        )
    }

    private fun createNotification() {
        notificationBuilder =
            Notification.Builder(mContext, NotificationChannels.FINISH_CANCEL_PRAYER)
        notificationBuilder.setSmallIcon(R.drawable.ic_more_app)

        notificationBuilder.setContentIntent(createPendingIntent())
    }

    private fun createPendingIntent(): PendingIntent? {
        val intent = Intent(mContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(
            mContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}