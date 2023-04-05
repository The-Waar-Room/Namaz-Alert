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

     fun setUpRTCAlarm(dataShare: String, notifyType:Int , timeInMillis:Long , pendingCode:Int) {
        val pendingAlarm =
            Intent(ACTION_FIRED)

                .apply {
                    setClass(mContext, BroadcastAlarmReceiver::class.java)
                        putExtra(DATA_SHARE_ID, dataShare)
                    putExtra(NOTIFY_TYPE,notifyType)
                }
                .let {

                    PendingIntent.getBroadcast(
                        mContext, pendingCode, it, pendingIntentUpdateCurrentFlag())
                }

        setAlarmStrategy.setRTCAlarm(timeInMillis, pendingAlarm)
    }

    fun removeRTCAlarm(pendingCode:Int ) {
        val pendingAlarm =
            PendingIntent.getBroadcast(
                mContext,
                pendingCode,
                Intent(ACTION_FIRED).apply {
                    // must be here, otherwise replace does not work
                    setClass(mContext, BroadcastAlarmReceiver::class.java)
                },
                pendingIntentUpdateCurrentFlag())
        am.cancel(pendingAlarm)
    }

    fun setInexactAlarm(dataShare: String, timeInMillis:Long) {

        Log.e("AlarmTAG", "setInexactAlarm")
        val pendingAlarm =
            Intent(ACTION_INEXACT_FIRED)
                .apply {
                    setClass(mContext, BroadcastAlarmReceiver::class.java)
                    putExtra(DATA_SHARE_ID, dataShare)
                }
                .let {
                    PendingIntent.getBroadcast(mContext, pendingInExactAlarmRequestCode, it, pendingIntentUpdateCurrentFlag())
                }

        setAlarmStrategy.setInexactAlarm(timeInMillis, pendingAlarm)
    }

    fun setInexactAlarmAlarmManger( timeInMillis:Long) {

        Log.e("AlarmTAG", "setInexactAlarm")
        val pendingAlarm =
            Intent(ACTION_INEXACT_FIRED_ALARM_MANAGER)
                .apply {
                    setClass(mContext, BroadcastAlarmReceiver::class.java)
                }
                .let {
                    PendingIntent.getBroadcast(mContext, pendingInExactAlarmRequestCode, it, pendingIntentUpdateCurrentFlag())
                }

        setAlarmStrategy.setInexactAlarm(timeInMillis, pendingAlarm)
    }

    fun removeInexactAlarm() {
        Log.e("AlarmTAG", "removeInexactAlarm ")

        val pendingAlarm =
            PendingIntent.getBroadcast(
                mContext,
                pendingInExactAlarmRequestCode,
                Intent(ACTION_INEXACT_FIRED).apply {
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

        override fun setInexactAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
            am.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }
    }

    @TargetApi(23)
    private inner class MarshmallowSetter : ISetAlarmStrategy {
        override fun setRTCAlarm(timeInMillis:Long , pendingIntent: PendingIntent) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }
        override fun setInexactAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
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
        override fun setInexactAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }

    }

    private interface ISetAlarmStrategy {
        fun setRTCAlarm( timeInMillis:Long, pendingIntent: PendingIntent)
        fun setInexactAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
            setRTCAlarm(timeInMillis, pendingIntent)
        }
    }


    companion object {
        const val ACTION_FIRED = BuildConfig.APPLICATION_ID + ".ACTION_FIRED"
        const val ACTION_INEXACT_FIRED = BuildConfig.APPLICATION_ID + ".ACTION_INEXACT_FIRED"
        const val ACTION_INEXACT_FIRED_ALARM_MANAGER = BuildConfig.APPLICATION_ID + ".ACTION_INEXACT_FIRED_ALARM_MANAGER"

        const val ACTION_CANCEL = BuildConfig.APPLICATION_ID + ".ACTION_CANCEL"
        const val ACTION_STOP = BuildConfig.APPLICATION_ID + ".ACTION_STOP"

        const val DATA_SHARE_ID = "intent.extra.data"
        const val NOTIFY_TYPE = "intent.extra.NOTIFY_TYPE"
        const val alertNotify = 1
        const val finishNotify = 2
        const val pendingExactAlertAlarmRequestCode = 0
        const val pendingExactFinishAlarmRequestCode = 1
        const val pendingInExactAlarmRequestCode = 2










    }
}