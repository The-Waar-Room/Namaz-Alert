package com.sudoajay.namaz_alert.ui.background

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.media.AudioManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.*
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.afterTimeID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.beforeTimeID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.phoneModeID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.prayerNameID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.prayerTimeID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.previousModeID
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
import com.sudoajay.namaz_alert.ui.notification.AlertNotification
import com.sudoajay.namaz_alert.ui.notification.NotificationChannels
import com.sudoajay.namaz_alert.util.Helper

class WorkMangerAlertNotification(var context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {



    lateinit var alertNotification: AlertNotification
    private lateinit var notificationCompat: NotificationCompat.Builder

    override fun doWork(): Result {
        alertNotification = AlertNotification(context)
        Log.e(
            "WorkManger", " get data - ${inputData.getString(prayerNameID)}  ," +
                    "${inputData.getString(prayerTimeID)} , ${inputData.getString(phoneModeID)} , " +
                    "${inputData.getString(beforeTimeID)} , ${inputData.getString(afterTimeID)}  ${inputData.getString(
                        previousModeID)}"
        )
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.ringerMode = Helper.getPhoneMode(inputData.getString(phoneModeID).toString())

        startNotification(inputData.getString(prayerNameID).toString() , inputData.getString(prayerTimeID).toString(),
            inputData.getString(phoneModeID).toString() , inputData.getString(beforeTimeID).toString(),
            inputData.getString(afterTimeID).toString(), inputData.getString(
            previousModeID).toString())


        return Result.success()
    }


    private fun startNotification(
        prayerName: String,
        prayerTime: String,
        phoneMode: String,
        beforeTime: String,
        afterTime: String,
        previousMode:String

    ) {
        createNotification()
        alertNotification.notifyCompat(
            prayerName, prayerTime, phoneMode,
            beforeTime, afterTime, previousMode,notificationCompat
        )
    }



    private fun createNotification() {
        notificationCompat =
            NotificationCompat.Builder(applicationContext, NotificationChannels.ALERT_PRAYER_TIME)
        notificationCompat.setSmallIcon(R.drawable.ic_more_app)

        notificationCompat.setContentIntent(createPendingIntent())
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