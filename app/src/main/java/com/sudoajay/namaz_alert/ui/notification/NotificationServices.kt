package com.sudoajay.namaz_alert.ui.notification

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
import com.sudoajay.namaz_alert.util.Command
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationServices: Service() {

    private var mBinder: IBinder = MyBinder()

    @Inject
    lateinit var alertNotification: AlertNotification
    private lateinit var notificationCompat: NotificationCompat.Builder
    private lateinit var notificationBuilder : Notification.Builder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(if (intent == null) Command.START else Command.values()[intent.getIntExtra(
            "COMMAND",
            Command.START.ordinal)]){

            Command.START -> {
                Log.i(TAG, "onStartCommand  Command.START -  $intent")

                startNotification()
            }

            Command.STOP ->{
                Log.i(TAG, "onStartCommand  Command.STOP -  $intent")
                stopNotification()
                stopSelf()
            }

            else -> {
                Log.i(TAG, "onStartCommand  Command.Setting -  $intent")
            }
        }
        return START_STICKY
    }


    private fun startNotification(){
        createNotification()
        alertNotification.notifyCompat(
            "Faujha","4:20 pm","Vibrate",
            "4:10 am" , "4:30 am", notificationCompat)
    }
    private fun createNotification(){
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

    private fun stopNotification(){
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
    companion object{
        const val NOTIFICATION_ALERT_STATE = 1
        const val NOTIFICATION_ALERT_SETTING = 2
        const val NOTIFICATION_ALERT_STOP = 3

        private const val TAG = "VpnService"
        const val commandNotification = "COMMAND"
        const val openSetting = "OpenSetting"
        const val stopAlert = "StopAlert"

        const val prayerNameId = "prayerName"
        const val prayerTimeId = "prayerTime"
        const val phoneModeId ="phoneMode"
    }
}