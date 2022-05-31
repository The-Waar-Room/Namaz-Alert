package com.sudoajay.namaz_alert.ui.background

import android.app.NotificationManager
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
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.diffTimeID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.phoneModeID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.prayerNameID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.prayerTimeID
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
import com.sudoajay.namaz_alert.ui.notification.AlertNotification
import com.sudoajay.namaz_alert.ui.notification.NotificationChannels
import com.sudoajay.namaz_alert.util.Helper
import kotlinx.coroutines.*

class WorkMangerAlertNotification(var context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {


    lateinit var alertNotification: AlertNotification
    private lateinit var notificationCompat: NotificationCompat.Builder
    private lateinit var protoManager: ProtoManager

    override fun doWork(): Result {
        alertNotification = AlertNotification(context)
        protoManager = ProtoManager(context)
        var notificationRingtone = 0

        Log.e(
            "WorkManger", " get data - ${inputData.getString(prayerNameID)}  ," +
                    "${inputData.getString(prayerTimeID)} , ${inputData.getString(phoneModeID)} , " +
                    "${inputData.getString(diffTimeID)} "
        )

        val previousMode = Helper.getRingerMode(context)

        CoroutineScope(Dispatchers.IO).launch {
            protoManager.setPreviousMode(previousMode)
        }

        CoroutineScope(Dispatchers.Main).launch {
            val waitFor = CoroutineScope(Dispatchers.IO).async {
                protoManager.setIsWorkMangerCancel(false)
                protoManager.setIsWorkMangerRunning(true)
                notificationRingtone = protoManager.fetchInitialPreferences().notificationRingtone

                return@async notificationRingtone
            }
            waitFor.await()
            startNotification(
                inputData.getString(prayerNameID).toString(),
                inputData.getString(prayerTimeID).toString(),
                inputData.getString(phoneModeID).toString(),
                inputData.getString(diffTimeID).toString(),
                previousMode,
                notificationRingtone
            )
            withContext(Dispatchers.IO) {
                if (notificationRingtone == 1) delay(1000 * 22) // 5 sec
                else delay(1000 * 5)// 5 sec
                if(Helper.doNotDisturbPermissionAlreadyGiven(context)) {
                    Helper.setRingerMode(context,Helper.getPhoneMode(inputData.getString(phoneModeID).toString()))
                }
            }
        }




        cancelAlertNotification()


        return Result.success()
    }

    private fun cancelAlertNotification() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(AlertNotification.NOTIFICATION_FinishCancel_STATE)
    }


    private fun startNotification(
        prayerName: String,
        prayerTime: String,
        phoneMode: String,
        diffTime: String,
        previousMode: String,notificationRingtone :Int

    ) {
        createNotification(prayerName, prayerTime)
        alertNotification.notifyCompat(
            prayerName, Helper.convertTo12Hr(context,prayerTime).toString(), phoneMode,
            diffTime, previousMode,notificationRingtone, notificationCompat
        )
    }


    private fun createNotification(
        prayerName: String,
        prayerTime: String
    ) {
        notificationCompat =
            NotificationCompat.Builder(applicationContext, NotificationChannels.ALERT_PRAYER_TIME)
        notificationCompat.setSmallIcon(R.drawable.app_icon)
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