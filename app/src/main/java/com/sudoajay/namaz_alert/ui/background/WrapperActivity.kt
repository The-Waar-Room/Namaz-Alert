package com.sudoajay.namaz_alert.ui.background

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.fragment.app.FragmentActivity
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.ui.notification.AlertNotification
import com.sudoajay.namaz_alert.ui.notification.NotificationChannels
import com.sudoajay.namaz_alert.util.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class WrapperActivity  : FragmentActivity()  {



    private lateinit var notificationCompat: NotificationCompat.Builder
    private lateinit var  alertNotification : AlertNotification
    private lateinit var notificationManager: NotificationManager


    private lateinit var dataShare :List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataShare = intent.getStringExtra(AlarmsScheduler.DATA_SHARE_ID).toString().split("||")

        alertNotification=AlertNotification(context = applicationContext)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        turnScreenOn()
        updateLayout()



    }

    private fun turnScreenOn() {
        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        // Deprecated flags are required on some devices, even with API>=27
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

            CoroutineScope(Dispatchers.Main).launch {
                delay(1000 * 60)
                openNewNotification()
                finish()

            }


    }


    private fun updateLayout() {

        setContentView(R.layout.activity_wrapper)

        findViewById<Button>(R.id.alert_button_ok).run {
            requestFocus()
            setOnClickListener {
                openNewNotification()
                finish()
            }
            setOnLongClickListener {
                openNewNotification()
                finish()
                true
            }
        }
        findViewById<Button>(R.id.alert_button_dismiss).run {
            setOnClickListener {
                text = getString(R.string.alarm_alert_hold_the_button_text)
            }
            setOnLongClickListener {
                sendBroadCast()
                finish()
                true
            }
        }

        setTitle()
        setTime()


    }

    private fun openNewNotification(){
        notificationManager.cancel(AlertNotification.NOTIFICATION_ALERT_STATE)

        val phoneMode = Helper.getPhoneMode(context = applicationContext)
        val notificationRingtone = Helper.getNotificationRingtone(applicationContext)
        startNotificationAlert(phoneMode,notificationRingtone,intent.getStringExtra(AlarmsScheduler.DATA_SHARE_ID).toString())
    }

    private fun setTitle() {
        val titleText = dataShare[0]
        title = titleText
        findViewById<TextView>(R.id.alarm_alert_label).text = titleText
    }

    private fun setTime(){
        findViewById<TextView>(R.id.digital_clock_time).text = Helper.convertTo12HrOnly(Helper.getCurrentTime())
        findViewById<TextView>(R.id.digital_clock_am_pm).text = Helper.getAMOrPM(applicationContext,Helper.getCurrentTime())
    }

    private fun sendBroadCast(){
        val cancelIntent = Intent(applicationContext, BroadcastAlarmReceiver::class.java)
        cancelIntent.action= AlarmsScheduler.ACTION_CANCEL
        cancelIntent.putExtra(AlarmsScheduler.DATA_SHARE_ID,intent.getStringExtra(AlarmsScheduler.DATA_SHARE_ID).toString())
        sendBroadcast(cancelIntent)
    }

    private fun startNotificationAlert(
        phoneMode: String,
        notificationRingtone: Int, dataShare: String
    ) {
        createNotificationAlert( dataShare)
        alertNotification.notifyCompat(
            phoneMode,
            notificationRingtone, notificationCompat,dataShare, notificationManager
        )
    }


    private fun createNotificationAlert(
        dataShare:String
    ) {
        notificationCompat =
            NotificationCompat.Builder(applicationContext, NotificationChannels.ALERT_DEFAULT_PRAYER_TIME  )
        notificationCompat.setSmallIcon(R.mipmap.ic_launcher)
        notificationCompat.setContentIntent(createWrapperPendingIntent(applicationContext, dataShare ))


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



    /**
     * Interface for getting the instance of binder from our service class
     * So client can get instance of our service class and can directly communicate with it.
     */

}