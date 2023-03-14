package com.sudoajay.namaz_alert.ui.foreground

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.model.Command
import com.sudoajay.namaz_alert.ui.BaseActivity.Companion.CommandTAG
import com.sudoajay.namaz_alert.ui.background.AlarmMangerForTask
import com.sudoajay.namaz_alert.ui.background.AlarmMangerForTask.Companion.prayerNameID
import com.sudoajay.namaz_alert.ui.background.AlarmMangerForTask.Companion.prayerTimeID
import com.sudoajay.namaz_alert.ui.background.AlarmMangerForTask.Companion.previousModeID
import com.sudoajay.namaz_alert.ui.background.AlarmsScheduler
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
import com.sudoajay.namaz_alert.ui.notification.AlertNotification
import com.sudoajay.namaz_alert.ui.notification.AlertNotification.Companion.NOTIFICATION_ALERT_Resume
import com.sudoajay.namaz_alert.ui.notification.AlertNotification.Companion.NOTIFICATION_ALERT_STATE
import com.sudoajay.namaz_alert.ui.notification.AlertNotification.Companion.NOTIFICATION_FinishCancel_STATE
import com.sudoajay.namaz_alert.ui.notification.NotificationChannels
import com.sudoajay.namaz_alert.ui.notification.UpComingNotification
import com.sudoajay.namaz_alert.ui.notification.UpComingNotification.Companion.NOTIFICATION_ID_STATE
import com.sudoajay.namaz_alert.util.Helper
import com.sudoajay.namaz_alert.util.Helper.Companion.getDiffSeconds
import kotlinx.coroutines.*

class ForegroundService : Service() {

    // Binder given to clients (notice class declaration below)
    private var mBinder: IBinder = MyBinder()

    private lateinit var notificationCompat: NotificationCompat.Builder
    private lateinit var notificationBuilder: Notification.Builder

    private var upComingNotification: UpComingNotification? = null
    private var alertNotification: AlertNotification? = null
    private var notificationJob: Job? = null
    private lateinit var protoManager: ProtoManager

    private var waitCoroutineRunning: Job? = null
    private var waitMainCoroutineRunning: Job? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var currentTime: String


        when (if (intent == null) Command.UPCOMING else Command.values()[intent.getIntExtra(
            CommandTAG,
            Command.UPCOMING.ordinal
        )]) {
            Command.UPCOMING -> {
                Log.e("ForegroundServiceTAG", "  UpComing ")
                Helper.setIsWorkMangerRunning(applicationContext,true)

                currentTime = Helper.getCurrentTimeWithSeconds()
                val arr = intent?.getStringExtra(AlarmsScheduler.DATA_SHARE_ID)?.split("||")
                Log.e(
                    "ForegroundServiceTAG" , " arr  here $arr   arr?.get(0)  ${arr?.get(0)}  arr?.get(1) $arr?.get(1)")

                startNotificationUpcoming(arr?.get(0) ?: "None", arr?.get(1) ?: "00:00")


                waitCoroutineRunning?.cancel()
                waitCoroutineRunning = CoroutineScope(Dispatchers.Main).launch {
                    Log.e(
                        "ForegroundServiceTAG",
                        "  getDiffSeconds(currentTime, arr?.get(3).toString()))  ${
                            getDiffSeconds(
                                currentTime,
                                arr?.get(3).toString()
                            )
                        }" +
                                "  currentTime ${currentTime}     arr?.get(3).toString()) ${
                                    arr?.get(
                                        3
                                    ).toString()
                                }     ${Helper.isWorkMangerRunning(applicationContext)}  +  Helper.isWorkMangerRunning()   "


                    )


                    delay((1000) * (getDiffSeconds(currentTime, arr?.get(3).toString())))
                    Log.e("ForegroundServiceTAG", "  Reach ")
                    cancelUpComingNotification()
                    stopSelf()
                    val alertIntent = Intent(applicationContext, ForegroundService::class.java)
                    if (intent != null) {
                        alertIntent.putExtra(
                            AlarmsScheduler.DATA_SHARE_ID,intent.getStringExtra(
                                AlarmsScheduler.DATA_SHARE_ID
                            ))

                    }
                    alertIntent.putExtra(CommandTAG, Command.ALERT.ordinal)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(alertIntent)
                    } else {
                        startService(alertIntent)
                    }


                }
                waitMainCoroutineRunning?.cancel()
                waitMainCoroutineRunning = CoroutineScope(Dispatchers.IO).launch {
                    delay((1000) * (getDiffSeconds(currentTime, arr?.get(4).toString())) + (5 * 60))
                    runAgainAlarmManager()
                }

            }
            Command.ALERT -> {
                Log.e(
                    "ForegroundServiceTAG",
                    "  comand ALERT ${Helper.isWorkMangerRunning(applicationContext)}  +  Helper.isWorkMangerRunning()   "
                )
                protoManager = ProtoManager(applicationContext)

                val arr = intent?.getStringExtra(AlarmsScheduler.DATA_SHARE_ID)?.split("||")

                val notificationRingtone = Helper.getNotificationRingtone(applicationContext)
                val previousMode = Helper.getRingerMode(applicationContext)
                val phoneMode = Helper.getPhoneMode(context = applicationContext)
                Helper.setPreviousPhoneMode(applicationContext, previousMode)


                startNotificationAlert(
                    arr?.get(0) ?: "None",
                    arr?.get(1) ?: "00:00",
                    phoneMode,
                    arr?.get(2) ?: "00:00",
                    previousMode,
                    notificationRingtone,
                    intent?.getStringExtra(AlarmsScheduler.DATA_SHARE_ID).toString()
                )
                waitCoroutineRunning?.cancel()
                waitCoroutineRunning = CoroutineScope(Dispatchers.Main).launch {
                    delay(1000 * 20)
                    if (Helper.doNotDisturbPermissionAlreadyGiven(applicationContext)) {
                        phoneMode.let { Helper.getPhoneMode(it) }.let {
                            Helper.setRingerMode(
                                applicationContext,
                                it
                            )
                        }
                    }
                }
                waitCoroutineRunning = CoroutineScope(Dispatchers.Main).launch {
                    currentTime = Helper.getCurrentTimeWithSeconds()
                    Log.e(
                        "ForegroundServiceTAG",
                        " (getDiffSeconds(currentTime, arr?.get(4).toString()))  ${
                            (getDiffSeconds(
                                currentTime,
                                arr?.get(4).toString()
                            ))
                        }" +
                                "  currentTime $currentTime    arr?.get(4).toString()) ${
                                    arr?.get(4).toString()
                                }     "
                    )
                    delay((1000) * (getDiffSeconds(currentTime, arr?.get(4).toString())))
                    Log.e(
                        "ForegroundServiceTAG",
                        "  Reach  ${Helper.isWorkMangerRunning(applicationContext)}  +  Helper.isWorkMangerRunning()  "
                    )


                    if (Helper.isWorkMangerRunning(applicationContext)) {
                        cancelUpComingNotification()
                        cancelUpFinishCancelNotification()
                        cancelUpAlertNotification()
                        waitCoroutineRunning?.cancel()
                        waitMainCoroutineRunning?.cancel()
                        stopSelf()

                        Helper.setIsWorkMangerRunning(applicationContext, false)

                        Log.e(
                            "ForegroundServiceTAG",
                            "  Reach  ${Helper.isWorkMangerRunning(applicationContext)}  +  Helper.isWorkMangerRunning()  "
                        )

                        if (intent != null) {
                            startNotificationCancelFinish(
                                getString(
                                    R.string.completed_the_notification_prayer,
                                    arr?.get(1) ?: ""
                                ),
                                getString(R.string.your_device_is_now_text, previousMode),
                                intent.getStringExtra(AlarmsScheduler.DATA_SHARE_ID).toString(),
                                true
                            )
                        }

                        if (Helper.doNotDisturbPermissionAlreadyGiven(applicationContext)) {
                            Helper.setRingerMode(
                                applicationContext,
                                Helper.getPhoneMode(previousMode)
                            )
                        }

                        Helper.setIsWorkMangerRunning(applicationContext, false)

                        runAgainAlarmManager()

                    }
                }



            }
            Command.CANCEL -> {
                Log.e("ForegroundServiceTAG", "  comand Cancel ")
                cancelUpComingNotification()
                waitCoroutineRunning?.cancel()
                waitMainCoroutineRunning?.cancel()
                cancelUpAlertNotification()
                stopSelf()

                val arr = intent?.getStringExtra(AlarmsScheduler.DATA_SHARE_ID)?.split("||")

                val previousMode = intent?.getStringExtra(previousModeID)

                if (Helper.doNotDisturbPermissionAlreadyGiven(applicationContext)) {
                    previousMode?.let { Helper.getPhoneMode(it) }?.let {
                        Helper.setRingerMode(
                            applicationContext,
                            it
                        )
                    }
                }
                if (intent != null) {
                    Log.e("ForegroundServiceTAG", "  here ntent.getStringExtra(\n" +
                            "                            AlarmsScheduler.DATA_SHARE_ID ${intent.getStringExtra(
                                AlarmsScheduler.DATA_SHARE_ID)} ")
                }
                startNotificationCancelFinish(
                    getString(
                        R.string.cancel_the_notification_prayer,
                        arr?.get(1) ?: ""
                    ),
                    getString(R.string.click_here_to_setup_text),
                    intent?.getStringExtra(AlarmsScheduler.DATA_SHARE_ID).toString(),
                    false
                )

                Helper.setIsWorkMangerRunning(applicationContext, false)
                currentTime = Helper.getCurrentTimeWithSeconds()


                waitMainCoroutineRunning = CoroutineScope(Dispatchers.Main).launch {
                    delay(1000*60)
                    runAgainAlarmManager()
                }


            }

            Command.RESUME -> {
                if (intent != null) {
                    Log.e("ForegroundServiceTAG", "  comand Resume intent.getStringExtra(\n" +
                            "                            AlarmsScheduler.DATA_SHARE_ID ${intent.getStringExtra(
                                AlarmsScheduler.DATA_SHARE_ID)} ")
                }

                cancelUpFinishCancelNotification()
                waitCoroutineRunning?.cancel()
                waitMainCoroutineRunning?.cancel()
                val alertIntent = Intent(applicationContext, ForegroundService::class.java)
                if (intent != null) {
                    alertIntent.putExtra(
                        AlarmsScheduler.DATA_SHARE_ID,intent.getStringExtra(
                            AlarmsScheduler.DATA_SHARE_ID
                        ))
                }
                alertIntent.putExtra(CommandTAG, Command.ALERT.ordinal)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(alertIntent)
                } else {
                    startService(alertIntent)
                }
                Helper.setIsWorkMangerRunning(applicationContext, true)


            }


            Command.STOP -> {
                Log.e("ForegroundServiceTAG", "  comand stop ")
                cancelUpComingNotification()
                cancelUpAlertNotification()
                cancelUpFinishCancelNotification()
                waitCoroutineRunning?.cancel()
                waitMainCoroutineRunning?.cancel()
                stopSelf()

                Helper.setIsWorkMangerRunning(applicationContext, false)
                runAgainAlarmManager()

            }
            else -> {

            }
        }

        return START_STICKY
    }

    private fun startNotificationUpcoming(
        prayerName: String,
        prayerTime: String

    ) {
        upComingNotification = UpComingNotification(context = applicationContext)

        createNotificationUpcoming(prayerName, prayerTime)
        upComingNotification?.notifyCompat(
            prayerName, Helper.convertTo12Hr(applicationContext, prayerTime), notificationCompat
        )


    }


    private fun createNotificationUpcoming(
        prayerName: String,
        prayerTime: String
    ) {
        notificationCompat =
            NotificationCompat.Builder(
                applicationContext,
                NotificationChannels.UPCOMING_PRAYER_TIME
            )
        notificationCompat.setSmallIcon(R.drawable.app_icon)
        notificationCompat.setContentIntent(createPendingIntent(prayerName, prayerTime))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            startForeground(
                NOTIFICATION_ID_STATE,
                notificationCompat.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE
            )
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground(NOTIFICATION_ID_STATE, notificationCompat.build())

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            notificationJob =   startCoroutineTimer {
//                notificationCompat.setWhen(System.currentTimeMillis())
//                notificationCompat.setShowWhen(true)
//                notificationCompat.setContentTitle("Hello Here")
//                Log.e("HereTag" , " Update My notficatiopn ")
//                upComingNotification.notification?.let { upComingNotification.notifyNotification(it) }
//
//            }
//        }

    }

//    @OptIn(DelicateCoroutinesApi::class)
//    private inline fun startCoroutineTimer(
//        repeatMillis: Long = 1000 * 10,
//        crossinline action: () -> Unit
//    ) = GlobalScope.launch {
//        while (true) {
//            action()
//            delay(repeatMillis)
//        }
//    }


    private fun startNotificationAlert(
        prayerName: String,
        prayerTime: String,
        phoneMode: String,
        diffTime: String,
        previousMode: String, notificationRingtone: Int,intentString:String

    ) {
        alertNotification = AlertNotification(applicationContext)
        createNotificationAlert(prayerName, prayerTime)
        alertNotification?.notifyCompat(
            prayerName, Helper.convertTo12Hr(applicationContext, prayerTime), phoneMode,
            diffTime, previousMode, notificationRingtone, notificationCompat,intentString
        )
    }


    private fun createNotificationAlert(
        prayerName: String,
        prayerTime: String
    ) {
        notificationCompat =
            NotificationCompat.Builder(applicationContext, NotificationChannels.ALERT_PRAYER_TIME)
        notificationCompat.setSmallIcon(R.drawable.app_icon)
        notificationCompat.setContentIntent(createPendingIntent(prayerName, prayerTime))


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            startForeground(
                NOTIFICATION_ALERT_STATE,
                notificationCompat.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE
            )
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground(NOTIFICATION_ALERT_STATE, notificationCompat.build())

    }

    private fun createPendingIntent(
        prayerName: String,
        prayerTime: String
    ): PendingIntent? {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(prayerNameID, prayerName)
        intent.putExtra(prayerTimeID, prayerTime)

        return PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_MUTABLE
        )
    }


    private fun startNotificationCancelFinish(
        title: String,
        subTitle: String,
        intentString: String, isTaskFinish: Boolean
    ) {


            Log.e("ForegroundServiceTAG", " startNotificationCancelFinish intentString ${intentString}  ")

        createNotificationCancelFinish(intentString, isTaskFinish)
        alertNotification?.notifyBuilder(
            title, subTitle, notificationBuilder
        )
    }

    private fun createNotificationCancelFinish(
        intentString: String, isTaskFinish: Boolean
    ) {
        val arr = intentString.split("||")
        Log.e("ForegroundServiceTAG", " createNotificationCancelFinish intentString ${intentString}  ")

        notificationBuilder =
            Notification.Builder(applicationContext, NotificationChannels.FINISH_CANCEL_PRAYER)
        notificationBuilder.setSmallIcon(R.drawable.app_icon)

        notificationBuilder.setContentIntent(
            if (isTaskFinish) createPendingIntent(
                arr[0],
                arr[1]
            ) else createPendingIntentCancelFinish(intentString)
        )
    }

    private fun createPendingIntentCancelFinish(
        intentString: String
    ): PendingIntent? {
        Log.e("ForegroundServiceTAG", " createPendingIntentCancelFinish intentString ${intentString}  ")

        val alertIntent = Intent(applicationContext, ForegroundService::class.java)
        alertIntent.putExtra(
            AlarmsScheduler.DATA_SHARE_ID,intentString
            )
        alertIntent.putExtra(CommandTAG, Command.RESUME.ordinal)



        return PendingIntent.getService(
            applicationContext, NOTIFICATION_ALERT_Resume, alertIntent,
            PendingIntent.FLAG_MUTABLE
        )
    }

    private  fun runAgainAlarmManager(){
        val workMangerForTask = AlarmMangerForTask(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            Helper.setIsWorkMangerRunning(applicationContext, false)
            workMangerForTask.startWorker()
        }
    }


    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }
    /**
     * Class used for the client Binder. The Binder object is responsible for returning an instance
     * of "MyService" to the client.
     */
    /**
     * Class used for the client Binder. The Binder object is responsible for returning an instance
     * of "MyService" to the client.
     */

    inner class MyBinder : Binder() {
        // Return this instance of MyService so clients can call public methods
        val service: ForegroundService
            get() =// Return this instance of MyService so clients can call public methods
                this@ForegroundService
    }


    override fun onDestroy() {
        cancelUpComingNotification()
        cancelUpAlertNotification()
        waitCoroutineRunning?.cancel()
        waitMainCoroutineRunning?.cancel()
        stopSelf()


    }

    private fun cancelUpComingNotification() {
        stopForeground(NOTIFICATION_ID_STATE)
        CoroutineScope(Dispatchers.IO).launch {
            notificationJob?.cancelAndJoin()
        }

        upComingNotification?.notificationManager?.cancel(NOTIFICATION_ID_STATE)
    }

    private fun cancelUpAlertNotification() {
        stopForeground(NOTIFICATION_ALERT_STATE)
        CoroutineScope(Dispatchers.IO).launch {
            notificationJob?.cancelAndJoin()
        }
        alertNotification?.notificationManager?.cancel(NOTIFICATION_ALERT_STATE)
    }

    private fun cancelUpFinishCancelNotification() {

        alertNotification?.notificationManager?.cancel(NOTIFICATION_FinishCancel_STATE)
    }


}