package com.sudoajay.namaz_alert.ui.background

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.media.AudioManager
import android.util.Log
import androidx.work.*
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.previousModeID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.subTitleNotificationID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.titleNotificationID
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
import com.sudoajay.namaz_alert.ui.notification.AlertNotification
import com.sudoajay.namaz_alert.ui.notification.NotificationChannels
import com.sudoajay.namaz_alert.util.Helper

class WorkMangerFinishNotification(var context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    lateinit var alertNotification: AlertNotification
    private lateinit var notificationBuilder: Notification.Builder

    override fun doWork(): Result {
        alertNotification = AlertNotification(context)
        Log.e(
            "WorkManger", " get data - ${inputData.getString(titleNotificationID).toString()} " +
                    " , ${inputData.getString(subTitleNotificationID).toString()  }  ringer mnde ${inputData.getString(
                        previousModeID).toString()}"
        )

        startNotification(inputData.getString(titleNotificationID).toString() ,inputData.getString(subTitleNotificationID).toString() )

//        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        am.ringerMode = Helper.getPhoneMode(inputData.getString(previousModeID).toString())

        return Result.success()
    }



    private fun startNotification(title:String ,subTitle:String) {
        createNotification()
        alertNotification.notifyBuilder(title,subTitle,notificationBuilder
        )
    }



    private fun createNotification() {
        notificationBuilder =
            Notification.Builder(applicationContext, NotificationChannels.FINISH_CANCEL_PRAYER)
        notificationBuilder.setSmallIcon(R.drawable.ic_more_app)

        notificationBuilder.setContentIntent(createPendingIntent())
    }

    private fun createPendingIntent(): PendingIntent? {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

}