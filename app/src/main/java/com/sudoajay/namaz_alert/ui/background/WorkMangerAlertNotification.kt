package com.sudoajay.namaz_alert.ui.background

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.media.AudioManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.diffTimeID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.phoneModeID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.prayerNameID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.prayerTimeID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.previousModeID
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
import com.sudoajay.namaz_alert.ui.notification.AlertNotification
import com.sudoajay.namaz_alert.ui.notification.NotificationChannels
import com.sudoajay.namaz_alert.util.Helper
import com.sudoajay.namaz_alert.util.PhoneMode
import kotlinx.coroutines.*

class WorkMangerAlertNotification(var context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {


    lateinit var alertNotification: AlertNotification
    private lateinit var notificationCompat: NotificationCompat.Builder

    override fun doWork(): Result {
        alertNotification = AlertNotification(context)
        Log.e(
            "WorkManger", " get data - ${inputData.getString(prayerNameID)}  ," +
                    "${inputData.getString(prayerTimeID)} , ${inputData.getString(phoneModeID)} , " +
                    "${inputData.getString(diffTimeID)} , ${
                        inputData.getString(
                            previousModeID
                        )
                    }"
        )


        startNotification(
            inputData.getString(prayerNameID).toString(),
            inputData.getString(prayerTimeID).toString(),
            inputData.getString(phoneModeID).toString(),
            inputData.getString(diffTimeID).toString(),
            inputData.getString(
                previousModeID
            ).toString()
        )
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000*22) // 22 sec
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.ringerMode = Helper.getPhoneMode(inputData.getString(phoneModeID).toString())
        }
        return Result.success()
    }




    private fun startNotification(
        prayerName: String,
        prayerTime: String,
        phoneMode: String,
        diffTime: String,
        previousMode: String

    ) {
        createNotification(prayerName,prayerTime)
        alertNotification.notifyCompat(
            prayerName, Helper.convertTo12Hours(prayerTime).toString(), phoneMode,
            diffTime, previousMode, notificationCompat
        )
    }


    private fun createNotification(
        prayerName: String,
        prayerTime: String
    ) {
        notificationCompat =
            NotificationCompat.Builder(applicationContext, NotificationChannels.ALERT_PRAYER_TIME)
        notificationCompat.setSmallIcon(R.drawable.ic_more_app)
        notificationCompat.setContentIntent(createPendingIntent(prayerName, prayerTime))
    }

    private fun createPendingIntent(
        prayerName: String,
        prayerTime: String
    ): PendingIntent? {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(prayerNameID, prayerName)
        intent.putExtra(prayerTimeID, prayerTime)

        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
    }

}