package com.sudoajay.namaz_alert.ui.notification

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.phoneModeID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.prayerNameID
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask.Companion.prayerTimeID
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
import com.sudoajay.namaz_alert.util.Command
import com.sudoajay.namaz_alert.util.Helper
import com.sudoajay.namaz_alert.util.Helper.Companion.convertTo12Hours
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class NotificationServices : Service() {

    private var mBinder: IBinder = MyBinder()

    @Inject
    lateinit var alertNotification: AlertNotification
    private lateinit var notificationCompat: NotificationCompat.Builder



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (if (intent == null) Command.START else Command.values()[intent.getIntExtra(
            "COMMAND",
            Command.START.ordinal
        )]) {

            Command.START -> {
                Log.i(TAG, "onStartCommand  Command.START -  $intent")

                val beforeTime = intent?.getStringExtra(
                    WorkMangerForTask.beforeTimeID).toString()
                val afterTime = intent?.getStringExtra(
                    WorkMangerForTask.afterTimeID
                ).toString()
                Log.i(TAG, "onStartCommand  beforeTime -  $beforeTime  ,  afterTime $afterTime")

                startNotification(
                    prayerName = intent?.getStringExtra(prayerNameID).toString(),
                    prayerTime = intent?.getStringExtra(
                        prayerTimeID
                    ).toString(),
                    phoneMode = intent?.getStringExtra(
                        phoneModeID
                    ).toString(),
                    beforeTime = convertTo12Hours(beforeTime).toString(),
                    afterTime = convertTo12Hours(afterTime).toString()
                )
                startCoroutinesTimer(beforeTime ,afterTime)



            }

            Command.STOP -> {
                Log.i(TAG, "onStartCommand  Command.STOP -  $intent")
                stopService()
            }

            else -> {
                Log.i(TAG, "onStartCommand  Command.Setting -  $intent")
            }
        }
        return START_STICKY
    }

    private fun stopService(){
        stopNotification()
        stopSelf()
    }

    private fun startCoroutinesTimer(beforeTime: String, afterTime: String){
        val minute = Helper.getDiffMinute(beforeTime,afterTime)
        Log.i(TAG, "onStartCommand  minute - $minute   millisecond ${minute*60000} ")
        CoroutineScope(Dispatchers.IO).launch {
            delay(100)
            stopService()
        }
    }

    private fun startNotification(
        prayerName: String,
        prayerTime: String,
        phoneMode: String,
        beforeTime: String,
        afterTime: String
    ) {
        createNotification()
//        alertNotification.notifyCompat(
//            prayerName, prayerTime, phoneMode,
//            beforeTime, afterTime, notificationCompat
//        )
    }

    private fun createNotification() {
        notificationCompat =
            NotificationCompat.Builder(applicationContext, NotificationChannels.ALERT_PRAYER_TIME)
        notificationCompat.setSmallIcon(R.drawable.ic_more_app)

        notificationCompat.setContentIntent(createPendingIntent())
    }

    private fun createPendingIntent(): PendingIntent? {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun stopNotification() {
        stopForeground(true)
        alertNotification.notificationManager?.cancelAll()
    }

    override fun onBind(p0: Intent?): IBinder {
        return mBinder
    }


    inner class MyBinder : Binder() {
        // Return this instance of MyService so clients can call public methods
        val service: NotificationServices
            get() =// Return this instance of MyService so clients can call public methods
                this@NotificationServices
    }

    companion object {


        private const val TAG = "VpnService"
        const val commandNotification = "COMMAND"
        const val openSetting = "OpenSetting"
        const val stopAlert = "StopAlert"


    }
}