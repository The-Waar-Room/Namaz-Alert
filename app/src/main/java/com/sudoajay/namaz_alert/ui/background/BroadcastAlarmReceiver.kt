package com.sudoajay.namaz_alert.ui.background

import android.app.AlarmManager
import android.content.*
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.sudoajay.namaz_alert.model.Command
import com.sudoajay.namaz_alert.ui.BaseActivity
import com.sudoajay.namaz_alert.ui.background.AlarmsScheduler.Companion.DATA_SHARE_ID
import com.sudoajay.namaz_alert.ui.foreground.ForegroundService
import com.sudoajay.namaz_alert.util.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BroadcastAlarmReceiver: BroadcastReceiver() {
    // Boolean to check if our activity is bound to service or not
    var mIsBound: Boolean = false

    var mService: ForegroundService? = null


    override fun onReceive(context: Context, intent: Intent) {

        Log.e("WorkManger", " Now its here Here " + intent.action)
        when (intent.action) {
            AlarmsScheduler.ACTION_FIRED -> {
                val serviceIntent = Intent(context, ForegroundService::class.java)
                serviceIntent.putExtra(DATA_SHARE_ID,intent.getStringExtra(DATA_SHARE_ID))
                serviceIntent.putExtra(BaseActivity.CommandTAG, Command.UPCOMING.ordinal)
                context.applicationContext.bindService(serviceIntent, serviceConnection,
                    Context.BIND_AUTO_CREATE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }

            }

            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_LOCALE_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED-> {
                val alarmManager = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val alarmsScheduler = AlarmsScheduler(alarmManager, context)
                alarmsScheduler.removeRTCAlarm()
                val workMangerForTask = AlarmMangerForTask(context)
                CoroutineScope(Dispatchers.IO).launch {
                    Helper.setIsWorkMangerRunning(context, false)
                    workMangerForTask.startWorker()
                }

            }


        }
    }

    /**
     * Interface for getting the instance of binder from our service class
     * So client can get instance of our service class and can directly communicate with it.
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {

            // We've bound to MyService, cast the IBinder and get MyBinder instance
            val binder = iBinder as ForegroundService.MyBinder
            mService = binder.service
            mIsBound = true

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mIsBound = false
        }
    }
}
