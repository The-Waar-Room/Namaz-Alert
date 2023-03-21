package com.sudoajay.namaz_alert.ui.background

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.WorkManager
import com.sudoajay.namaz_alert.data.db.DailyPrayerDatabase
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.data.repository.DailyPrayerRepository
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

    suspend fun startWorker() {
        protoManager = ProtoManager(context)
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmsScheduler = AlarmsScheduler(alarmManager, context)


        val isWorkManagerRunning = Helper.isAlarmMangerRunning(context)

        Log.e(
            "WorkManger",
            "Reminder service alarm is " + (if (isReminderServiceAlarmSet(context)) "" else "not ") + "set already"
        )
        if (!isWorkManagerRunning || !isReminderServiceAlarmSet(context)  ) {
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


//                val data = workDataOf(
//                    prayerNameID to dailyPrayerDB.Name,
//                    prayerTimeID to dailyPrayerDB.Time,
//                    diffTimeID to diffTime,
////                    startTimeID to "$startTime:00" ,
////                    endTimeID  to "$endTime:00"
//                    )
//                Log.e("WorkManger", "diffTime ${data.getString(diffTimeID)} ")
//
//                val workManager = WorkManager.getInstance(context)
//                val constraints = Constraints.Builder()
//
//
//                val workMangerUpComingNotification =
//                    OneTimeWorkRequestBuilder<WorkMangerUpComingNotification>()
////                        .setInitialDelay((getDiffMinute(currentTime,startTime)-timeGapInEveryWhere) * 1000*60, TimeUnit.MILLISECONDS)
//
//                        .setInitialDelay(1000*60*2, TimeUnit.MILLISECONDS)
//                        .addTag(workMangerTAGID)
//                        .setInputData(data)
//                        .setConstraints(constraints.build())
//                        .build()
//
//                workManager.beginWith(workMangerUpComingNotification).enqueue()
//                workManager.beginUniqueWork(
//                    workMangerTAGID,
//                    ExistingWorkPolicy.REPLACE,
//                    OneTimeWorkRequest.from(WorkMangerUpComingNotification::class.java)
//                )

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
                        System.currentTimeMillis() + ( 1000 * 10   )
                    )

                    alarmsScheduler.setUpRTCAlarm(
                        dataShare,
                        alertNotify,
                        System.currentTimeMillis() + (1000 * 20   ),
                        pendingExactAlertAlarmRequestCode
                    )
                    alarmsScheduler.setUpRTCAlarm(
                        dataShare,
                        finishNotify,
                        System.currentTimeMillis() + (1000 * 50  ),
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

    private fun isReminderServiceAlarmSet(context: Context): Boolean {
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


    companion object {
        const val prayerNameID = "PrayerNameID"
        const val prayerTimeID = "PrayerTimeID"
        const val diffTimeID = "DiffTimeID"
        const val startTimeID = "StartTimeID"
        const val endTimeID = "EndTimeID"


        const val timeGapInEveryWhere = 10

        const val titleNotificationID = "TitleNotificationID"
        const val subTitleNotificationID = "SubTitleNotificationID"
        const val previousModeID = "PreviousModeID"

        const val workMangerTAGID = "WorkMangerID"


    }
}