package com.sudoajay.namaz_alert.ui.background

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.sudoajay.namaz_alert.BuildConfig
import com.sudoajay.namaz_alert.ui.BaseActivity
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
import java.util.*

class AlarmsScheduler(private val am: AlarmManager, private val mContext: Context
) {
    private val setAlarmStrategy: ISetAlarmStrategy

    init {
        this.setAlarmStrategy = initSetStrategyForVersion()
    }

     fun setUpRTCAlarm(dataShare: String , timeInMillis:Long) {
        val pendingAlarm =
            Intent(ACTION_FIRED)

                .apply {
                    setClass(mContext, BroadcastAlarmReceiver::class.java)
                        putExtra(DATA_SHARE_ID, dataShare)
                }
                .let {

                    PendingIntent.getBroadcast(
                        mContext, pendingAlarmRequestCode, it, pendingIntentUpdateCurrentFlag())
                }

        setAlarmStrategy.setRTCAlarm(timeInMillis, pendingAlarm)
    }

    fun removeRTCAlarm() {
        val pendingAlarm =
            PendingIntent.getBroadcast(
                mContext,
                pendingAlarmRequestCode,
                Intent(ACTION_FIRED).apply {
                    // must be here, otherwise replace does not work
                    setClass(mContext, BroadcastAlarmReceiver::class.java)
                },
                pendingIntentUpdateCurrentFlag())
        am.cancel(pendingAlarm)
    }



    fun pendingIntentUpdateCurrentFlag(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    }

    private fun initSetStrategyForVersion(): ISetAlarmStrategy {
        return when {
            Build.VERSION.SDK_INT >= 26 -> OreoSetter()
            Build.VERSION.SDK_INT >= 23 -> MarshmallowSetter()
            else -> KitKatSetter()
        }
    }


    private inner class KitKatSetter : ISetAlarmStrategy {
        override fun setRTCAlarm( timeInMillis:Long , pendingIntent: PendingIntent) {
            am.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }


    }

    @TargetApi(23)
    private inner class MarshmallowSetter : ISetAlarmStrategy {
        override fun setRTCAlarm(timeInMillis:Long , pendingIntent: PendingIntent) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }

    }

    /** 8.0 */
    @TargetApi(Build.VERSION_CODES.O)
    private inner class OreoSetter : ISetAlarmStrategy {
        override fun setRTCAlarm(timeInMillis:Long , pendingIntent: PendingIntent) {
            Log.e("WorkManger", " Now its here Here  OreoSetter  timeInMillis $timeInMillis")


            val pendingShowList =
                PendingIntent.getActivity(
                    mContext,
                    0,
                    Intent(mContext, BaseActivity::class.java),
                    pendingIntentUpdateCurrentFlag())
            am.setAlarmClock(
                AlarmManager.AlarmClockInfo(timeInMillis, pendingShowList), pendingIntent)
        }


    }

    private interface ISetAlarmStrategy {
        fun setRTCAlarm( timeInMillis:Long, pendingIntent: PendingIntent)

    }


    companion object {
        const val ACTION_FIRED = BuildConfig.APPLICATION_ID + ".ACTION_FIRED"
        private val pendingAlarmRequestCode = 0

        const val DATA_SHARE_ID = "intent.extra.data"

    }
}