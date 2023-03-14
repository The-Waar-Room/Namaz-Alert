package com.sudoajay.namaz_alert.ui.background

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sudoajay.namaz_alert.model.Command
import com.sudoajay.namaz_alert.ui.BaseActivity
import com.sudoajay.namaz_alert.ui.background.AlarmMangerForTask.Companion.diffTimeID
import com.sudoajay.namaz_alert.ui.background.AlarmMangerForTask.Companion.endTimeID
import com.sudoajay.namaz_alert.ui.background.AlarmMangerForTask.Companion.prayerNameID
import com.sudoajay.namaz_alert.ui.background.AlarmMangerForTask.Companion.prayerTimeID
import com.sudoajay.namaz_alert.ui.background.AlarmMangerForTask.Companion.startTimeID
import com.sudoajay.namaz_alert.ui.foreground.ForegroundService

class WorkMangerUpComingNotification(var context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    // Boolean to check if our activity is bound to service or not
    var mIsBound: Boolean = false

    var mService: ForegroundService? = null

    override fun doWork(): Result {

        val serviceIntent = Intent(context, ForegroundService::class.java)
        serviceIntent.action =
            inputData.getString(prayerNameID) + "||" + inputData.getString(prayerTimeID) + "||" + inputData.getString(
                diffTimeID )+"||" + inputData.getString(startTimeID) +"||" + inputData.getString(endTimeID)
        serviceIntent.putExtra(BaseActivity.CommandTAG, Command.UPCOMING.ordinal)

        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }


        return Result.success()
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