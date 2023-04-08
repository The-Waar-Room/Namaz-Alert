package com.sudoajay.namaz_alert.ui.background

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.map
import androidx.work.*
import com.sudoajay.namaz_alert.data.db.DailyPrayerDatabase
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.data.repository.DailyPrayerRepository
import com.sudoajay.namaz_alert.data.repository.WebScrappingGoogle
import com.sudoajay.namaz_alert.ui.background.AlarmsScheduler.Companion.alertNotify
import com.sudoajay.namaz_alert.ui.background.AlarmsScheduler.Companion.finishNotify
import com.sudoajay.namaz_alert.ui.background.AlarmsScheduler.Companion.pendingExactAlertAlarmRequestCode
import com.sudoajay.namaz_alert.ui.background.AlarmsScheduler.Companion.pendingExactFinishAlarmRequestCode
import com.sudoajay.namaz_alert.ui.notification.AlertNotification
import com.sudoajay.namaz_alert.util.Helper
import com.sudoajay.namaz_alert.util.Helper.Companion.doesDatabaseExist
import com.sudoajay.namaz_alert.util.Helper.Companion.getCurrentTime
import com.sudoajay.namaz_alert.util.Helper.Companion.getCurrentTimeWithSeconds
import com.sudoajay.namaz_alert.util.Helper.Companion.getDiffMinute
import com.sudoajay.namaz_alert.util.Helper.Companion.getMeIncrementTime
import com.sudoajay.namaz_alert.util.Helper.Companion.getPrayerGapTime
import com.sudoajay.namaz_alert.util.Helper.Companion.getTodayDate
import com.sudoajay.namaz_alert.util.Helper.Companion.getTomorrowDate
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs


class AlarmMangerForTask @Inject constructor(var context: Context) {

    private lateinit var protoManager: ProtoManager
    private var dailyPrayerDoa = DailyPrayerDatabase.getDatabase(context).dailyPrayerDoa()
    private lateinit var dailyPrayerRepository: DailyPrayerRepository
    private var prayerGapTime: String = ""
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmsScheduler: AlarmsScheduler
    private lateinit var notificationManager:NotificationManager


    lateinit var webScrappingGoogle: WebScrappingGoogle

    suspend fun startWorker() {
        protoManager = ProtoManager(context)
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmsScheduler = AlarmsScheduler(alarmManager, context)
        webScrappingGoogle = WebScrappingGoogle(context)


        webScrappingGoogle.checkEvertTimeIfDataIsUpdated()
        val isWorkManagerRunning = Helper.isAlarmMangerRunning(context)

        Log.e(
            "WorkManger",
            "Reminder service alarm is " + (if (isReminderFinishAlarmSet(context)) "" else "not ") + "set already"
        )
        if (!isWorkManagerRunning || !isReminderFinishAlarmSet(context)  ) {
            WorkManager.getInstance(context).cancelAllWork()
            notificationManager=
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            cancelNotificationEverything()
            stopEverything()

            //        Creating Object and Initialization
            dailyPrayerRepository = DailyPrayerRepository(dailyPrayerDoa)

            Log.e(
                "WorkManger",
                " getMeIncrementTime(getCurrentTime(), timeGapInEveryWhere) +  ${
                    getMeIncrementTime(
                        getCurrentTime(),
                        timeGapInEveryWhere
                    )
                }  getCurrentTime() + ${getCurrentTime()}   +"
            )
            if (doesDatabaseExist(context, dailyPrayerRepository)) {
                val currentTime = getCurrentTime()
                val dailyPrayerDB = dailyPrayerRepository.getNextTime(
                    getTodayDate(),
                    getTomorrowDate(), getMeIncrementTime(currentTime, timeGapInEveryWhere)
                )


                val waitFor = CoroutineScope(Dispatchers.IO).async {
                    prayerGapTime = getPrayerGapTime(dailyPrayerDB.Name, protoManager)
                    return@async prayerGapTime
                }
                waitFor.await()

                Log.e(
                    "WorkManger",
                    "prayerGapTime - $prayerGapTime  ${Helper.isAlarmMangerRunning(context)} "
                )
                val arrayIncrement = prayerGapTime.split(":")
                val startTime = getMeIncrementTime(dailyPrayerDB.Time, (arrayIncrement[0].toInt()))
                val endTime = getMeIncrementTime(dailyPrayerDB.Time, arrayIncrement[1].toInt())
                val diffTime =
                    (arrayIncrement[1].toInt() + abs(arrayIncrement[0].toInt())).toString()

                Log.e("WorkManger", "diffTime $diffTime ")

                Helper.setIsAlarmMangerRunning(context,true)



                     WorkManager.getInstance(context)
                    .getWorkInfosForUniqueWorkLiveData(workMangerTAGID)
                    .map { it.lastOrNull() }
                    .map { it?.state == WorkInfo.State.ENQUEUED || it?.state == WorkInfo.State.RUNNING || it?.state != WorkInfo.State.BLOCKED  }
                    .map {
                            if(!it) runWorkManger()
                        it
                    }

//                 System.currentTimeMillis()+25*24*60*60*1000l
                val dataShare =
                    dailyPrayerDB.Name + "||" + dailyPrayerDB.Time + "||" + diffTime + "||" + "$startTime:00" + "||" + "$endTime:00"



                if ((Helper.getDiffSeconds(
                        getCurrentTimeWithSeconds(), "$startTime:00"
                    ) > 0)
                ) {
//                    alarmsScheduler.setInexactAlarm(
//                        dataShare,
//                        System.currentTimeMillis() + ( 1000 * 60 * (getDiffMinute(currentTime,startTime)-timeGapInEveryWhere)  )
//                    )
//
//                    alarmsScheduler.setUpRTCAlarm(
//                        dataShare,
//                        alertNotify,
//                        System.currentTimeMillis() + (1000 * 60 * (getDiffMinute(currentTime,startTime))  ),
//                        pendingExactAlertAlarmRequestCode
//                    )
//                    alarmsScheduler.setUpRTCAlarm(
//                        dataShare,
//                        finishNotify,
//                        System.currentTimeMillis() + (1000 * 60 * (getDiffMinute(currentTime,endTime))  ),
//                        pendingExactFinishAlarmRequestCode
//                    )

                    alarmsScheduler.setInexactAlarm(
                        dataShare,
                        System.currentTimeMillis() + ( 1000 * 5   )
                    )

                    alarmsScheduler.setUpRTCAlarm(
                        dataShare,
                        alertNotify,
                        System.currentTimeMillis() + (1000 * 10   ),
                        pendingExactAlertAlarmRequestCode
                    )
                    alarmsScheduler.setUpRTCAlarm(
                        dataShare,
                        finishNotify,
                        System.currentTimeMillis() + (1000 * 90  ),
                        pendingExactFinishAlarmRequestCode
                    )
                }
                else {
                    alarmsScheduler.setInexactAlarmAlarmManger(
                        System.currentTimeMillis() + ( 1000 * 60 * timeGapInEveryWhere  )
                    )

                }


            } else {
                Log.e("WorkManger", " still not found db")
            }
        }

    }

    private fun stopEverything(){
        alarmsScheduler.removeInexactAlarm()
        alarmsScheduler.removeRTCAlarm(pendingExactAlertAlarmRequestCode)
        alarmsScheduler.removeRTCAlarm(AlarmsScheduler.pendingInExactAlarmRequestCode)


    }


    private fun cancelNotificationEverything(){
        notificationManager.cancel(AlertNotification.NOTIFICATION_UPCOMING_STATE)
        notificationManager.cancel(AlertNotification.NOTIFICATION_ALERT_STATE)
        if(Helper.isAlarmMangerCancel(context))
            notificationManager.cancel(AlertNotification.NOTIFICATION_FinishCancel_STATE)

    }

    private fun isReminderFinishAlarmSet(context: Context): Boolean {
        val intent = Intent(context.applicationContext, BroadcastAlarmReceiver::class.java)
        intent.action = AlarmsScheduler.ACTION_FIRED
        val isBackupServiceAlarmSet: Boolean
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                context.applicationContext,
                pendingExactFinishAlarmRequestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
            )
            isBackupServiceAlarmSet = PendingIntent.getBroadcast(
                context.applicationContext,
                pendingExactFinishAlarmRequestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
            ) != null
        } else {
            PendingIntent.getBroadcast(
                context.applicationContext,
                pendingExactFinishAlarmRequestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE
            )
            isBackupServiceAlarmSet = PendingIntent.getBroadcast(
                context.applicationContext,
                pendingExactFinishAlarmRequestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE
            ) != null
        }

        return isBackupServiceAlarmSet
    }

    private fun runWorkManger(){
        val workManager = WorkManager.getInstance(context)

        val workMangerNotification =
            PeriodicWorkRequestBuilder<WorkMangerNotification>(3, TimeUnit.HOURS)
                .addTag(workMangerTAGID)
                .build()

        workManager.enqueueUniquePeriodicWork(
            workMangerTAGID,
            ExistingPeriodicWorkPolicy.KEEP,
            workMangerNotification
        )

    }


    companion object {
        const val prayerNameID = "PrayerNameID"
        const val prayerTimeID = "PrayerTimeID"
        const val timeGapInEveryWhere = 10

        const val workMangerTAGID = "WorkMangerID"


    }
}