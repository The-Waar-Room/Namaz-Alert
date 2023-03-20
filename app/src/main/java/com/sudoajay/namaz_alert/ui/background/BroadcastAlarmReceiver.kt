package com.sudoajay.namaz_alert.ui.background

import android.app.*
import android.content.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.ui.background.AlarmsScheduler.Companion.NOTIFY_TYPE
import com.sudoajay.namaz_alert.ui.background.AlarmsScheduler.Companion.alertNotify
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
import com.sudoajay.namaz_alert.ui.notification.AlertNotification
import com.sudoajay.namaz_alert.ui.notification.AlertNotification.Companion.NOTIFICATION_ALERT_STATE
import com.sudoajay.namaz_alert.ui.notification.AlertNotification.Companion.NOTIFICATION_FinishCancel_STATE
import com.sudoajay.namaz_alert.ui.notification.AlertNotification.Companion.NOTIFICATION_UPCOMING_STATE
import com.sudoajay.namaz_alert.ui.notification.NotificationChannels
import com.sudoajay.namaz_alert.ui.notification.UpComingNotification
import com.sudoajay.namaz_alert.util.Helper
import kotlinx.coroutines.*


class BroadcastAlarmReceiver: BroadcastReceiver() {
    // Boolean to check if our activity is bound to service or not


    private lateinit var notificationCompat: NotificationCompat.Builder
    private var waitCoroutineRunning: Job? = null
    private lateinit var  alertNotification :AlertNotification
    private lateinit var upComingNotification:UpComingNotification
    private lateinit var notificationManager:NotificationManager

    override fun onReceive(context: Context, intent: Intent) {

        alertNotification=AlertNotification(context = context)
        upComingNotification = UpComingNotification(context)
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



        Log.e("WorkManger", " Now its here Here " + intent.action)
        when (intent.action) {
            AlarmsScheduler.ACTION_INEXACT_FIRED -> {
                cancelNotificationEverything()
                val dataShare = intent.getStringExtra(AlarmsScheduler.DATA_SHARE_ID)?.split("||")

                startNotificationUpcoming(context,dataShare?.get(0) ?: "None", dataShare?.get(1) ?: "00:00")


            }
            AlarmsScheduler.ACTION_FIRED -> {
                cancelNotificationEverything()
                val previousMode = Helper.getPreviousPhoneMode(context)
                val dataShare  = intent.getStringExtra(AlarmsScheduler.DATA_SHARE_ID)?.split("||")
                if(intent.getIntExtra(NOTIFY_TYPE,1) == 1) {

                    Helper.setIsAlarmMangerRunning(context,true)
                    Helper.setIsAlarmMangerCancel(context, false)
                    val notificationRingtone = Helper.getNotificationRingtone(context)
                    val phoneMode = Helper.getPhoneMode(context = context)
                    Helper.setPreviousPhoneMode(context, previousMode)

                    startNotificationAlert(
                        context,
                        phoneMode,
                        notificationRingtone,
                        intent.getStringExtra(AlarmsScheduler.DATA_SHARE_ID).toString()
                    )

                    waitCoroutineRunning = CoroutineScope(Dispatchers.IO).launch{
                        delay(1000 * 20)
                        if (Helper.doNotDisturbPermissionAlreadyGiven(context)) {
                            phoneMode.let { Helper.getPhoneMode(it) }.let {
                                Helper.setRingerMode(
                                    context,
                                    it
                                )
                            }
                        }
                    }
                }else{
                    Log.e("WorkManger" , "checkIf  Helper.isAlarmMangerCancel(context) " + Helper.isAlarmMangerCancel(context) )

                    cancelNotificationEverything()
                    if(!Helper.isAlarmMangerCancel(context)) {
                        startNotificationCancelFinish(
                            context,
                            context.getString(
                                R.string.completed_the_notification_prayer,
                                dataShare?.get(1) ?: ""
                            ),
                            context.getString(R.string.your_device_is_now_text, previousMode),
                            intent.getStringExtra(AlarmsScheduler.DATA_SHARE_ID).toString(),
                            true
                        )
                    }
                    if (Helper.doNotDisturbPermissionAlreadyGiven(context)) {
                        Helper.setRingerMode(
                            context,
                            Helper.getPhoneMode(previousMode)
                        )
                    }

                    Helper.setIsAlarmMangerRunning(context, false)
                    runAgainAlarmManager(context)


                }

            }

            AlarmsScheduler.ACTION_CANCEL->{
                Helper.setIsAlarmMangerRunning(context, false)
                Helper.setIsAlarmMangerCancel(context, true)
                val dataShare  = intent.getStringExtra(AlarmsScheduler.DATA_SHARE_ID)?.split("||")

                cancelNotificationEverything()
                val previousMode = Helper.getPreviousPhoneMode(context)

                if (Helper.doNotDisturbPermissionAlreadyGiven(context)) {
                    previousMode.let { Helper.getPhoneMode(it) }.let {
                        Helper.setRingerMode(
                            context,
                            it
                        )
                    }
                }

                startNotificationCancelFinish(context,
                    context.getString(
                        R.string.cancel_the_notification_prayer,
                        dataShare?.get(1) ?: ""
                    ),
                    context.getString(R.string.click_here_to_setup_text),
                    intent.getStringExtra(AlarmsScheduler.DATA_SHARE_ID).toString(),
                    false
                )



            }

            AlarmsScheduler.ACTION_STOP->{
                cancelNotificationEverything()
                Helper.setIsAlarmMangerRunning(context, false)
                runAgainAlarmManager(context)
            }

            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_LOCALE_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            AlarmsScheduler.ACTION_INEXACT_FIRED_ALARM_MANAGER,
            Intent.ACTION_TIME_CHANGED-> {
                cancelNotificationEverything()
                Helper.setIsAlarmMangerRunning(context, false)
                runAgainAlarmManager(context)

            }



        }
    }



    private fun cancelNotificationEverything(){
        cancelUpComingNotification()
        cancelUpFinishCancelNotification()
        cancelUpAlertNotification()
        waitCoroutineRunning?.cancel()
    }


    private  fun runAgainAlarmManager(context: Context){
        val workMangerForTask = AlarmMangerForTask(context = context)
        CoroutineScope(Dispatchers.IO).launch {
            Helper.setIsAlarmMangerRunning(context, false)
            workMangerForTask.startWorker()
        }
    }

    private fun startNotificationUpcoming(
        context: Context,
        prayerName: String,
        prayerTime: String

    ) {
        upComingNotification = UpComingNotification(context = context)

        createNotificationUpcoming(context,prayerName, prayerTime)
        upComingNotification.notifyCompat(
            prayerName, Helper.convertTo12Hr(context, prayerTime), notificationCompat, notificationManager
        )


    }

    private fun createNotificationUpcoming(context: Context,
        prayerName: String,
        prayerTime: String
    ) {
        notificationCompat =
            NotificationCompat.Builder(
                context,
                NotificationChannels.UPCOMING_PRAYER_TIME
            )
        notificationCompat.setSmallIcon(R.drawable.app_icon)
        notificationCompat.setContentIntent(createPendingIntent(context = context,prayerName, prayerTime))


    }
    private fun startNotificationAlert(
        context: Context,
        phoneMode: String,
         notificationRingtone: Int, dataShare: String


    ) {
        createNotificationAlert(context, dataShare)
        alertNotification.notifyCompat(
            phoneMode,
             notificationRingtone, notificationCompat,dataShare, notificationManager
        )
    }


    private fun createNotificationAlert(
        context: Context,
       dataShare:String
    ) {
        notificationCompat =
            NotificationCompat.Builder(context, NotificationChannels.ALERT_PRAYER_TIME)
        notificationCompat.setSmallIcon(R.drawable.app_icon)
        notificationCompat.setContentIntent(createWrapperPendingIntent(context, dataShare ))
        notificationCompat.setFullScreenIntent(createWrapperPendingIntent(context,dataShare), true)

        
    }

    private fun createWrapperPendingIntent(
        context: Context,
        dataShare:String

    ): PendingIntent? {

        val intent = Intent(context, WrapperActivity::class.java)
        intent.putExtra(AlarmsScheduler.DATA_SHARE_ID, dataShare)

        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_MUTABLE
        )
    }


    private fun startNotificationCancelFinish(
        context: Context,
        title: String,
        subTitle: String,
        intentString: String, isTaskFinish: Boolean
    ) {
        Log.e("ForegroundServiceTAG", " startNotificationCancelFinish intentString $intentString  ")

        createNotificationCancelFinish(context,intentString, isTaskFinish)
        alertNotification.notifyCompatCancelAndFinish(
            title, subTitle, notificationCompat, notificationManager
        )
    }

    private fun createNotificationCancelFinish(  context: Context,
        intentString: String, isTaskFinish: Boolean
    ) {
        val arr = intentString.split("||")
        Log.e("ForegroundServiceTAG", " createNotificationCancelFinish intentString ${intentString}  ")

        notificationCompat =
            NotificationCompat.Builder(context, NotificationChannels.FINISH_CANCEL_PRAYER)
        notificationCompat.setSmallIcon(R.drawable.app_icon)

        notificationCompat.setContentIntent(
            if (isTaskFinish) createPendingIntent( context = context,
                arr[0],
                arr[1]
            ) else createPendingIntentCancelFinish(context,intentString)
        )
    }

    private fun createPendingIntentCancelFinish(  context: Context,
        intentString: String
    ): PendingIntent? {
        Log.e("ForegroundServiceTAG", " createPendingIntentCancelFinish intentString ${intentString}  ")

        val alertIntent = Intent(context, BroadcastAlarmReceiver::class.java)
        alertIntent.putExtra(
            AlarmsScheduler.DATA_SHARE_ID,intentString
        )
        alertIntent.action =  AlarmsScheduler.ACTION_FIRED
        alertIntent.putExtra(NOTIFY_TYPE ,alertNotify)



        return PendingIntent.getBroadcast(
            context, AlertNotification.NOTIFICATION_ALERT_Resume, alertIntent,
            PendingIntent.FLAG_MUTABLE
        )
    }


    private fun createPendingIntent(
        context: Context,
        prayerName: String,
        prayerTime: String
    ): PendingIntent? {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(AlarmMangerForTask.prayerNameID, prayerName)
        intent.putExtra(AlarmMangerForTask.prayerTimeID, prayerTime)

        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_MUTABLE
        )
    }

    private fun cancelUpComingNotification() {
        notificationManager.cancel(NOTIFICATION_UPCOMING_STATE)
    }

    private fun cancelUpAlertNotification() {

        notificationManager.cancel(NOTIFICATION_ALERT_STATE)
    }

    private fun cancelUpFinishCancelNotification() {

        notificationManager.cancel(NOTIFICATION_FinishCancel_STATE)
    }

}