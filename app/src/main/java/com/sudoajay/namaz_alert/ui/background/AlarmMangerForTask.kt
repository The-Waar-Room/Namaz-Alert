package com.sudoajay.namaz_alert.ui.background

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.work.WorkManager
import com.sudoajay.namaz_alert.data.db.DailyPrayerDatabase
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.data.repository.DailyPrayerRepository
import com.sudoajay.namaz_alert.ui.notification.AlertNotification
import com.sudoajay.namaz_alert.ui.notification.UpComingNotification
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

    suspend fun startWorker() {
        protoManager = ProtoManager(context)
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val isWorkManagerRunning = Helper.isWorkMangerRunning(context)
//        val waitWorkManagerRunning = CoroutineScope(Dispatchers.IO).async {
//            isWorkManagerRunning = protoManager.fetchInitialPreferences().isWorkMangerRunning
//            return@async isWorkManagerRunning
//        }
//        waitWorkManagerRunning.await()
        if (!isWorkManagerRunning) {
            WorkManager.getInstance(context).cancelAllWork()
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(UpComingNotification.NOTIFICATION_ID_STATE)
            manager.cancel(AlertNotification.NOTIFICATION_ALERT_STATE)
            manager.cancel(AlertNotification.NOTIFICATION_FinishCancel_STATE)

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
                    "prayerGapTime - $prayerGapTime  ${Helper.isWorkMangerRunning(context)} "
                )
                val arrayIncrement = prayerGapTime.split(":")
                val startTime = getMeIncrementTime(dailyPrayerDB.Time, (arrayIncrement[0].toInt()))
                val endTime = getMeIncrementTime(dailyPrayerDB.Time, arrayIncrement[1].toInt())
                val diffTime =
                    (arrayIncrement[1].toInt() + abs(arrayIncrement[0].toInt())).toString()

                Log.e("WorkManger", "diffTime $diffTime ")

                Helper.setIsWorkMangerRunning(context,true)


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

                val alarmsScheduler = AlarmsScheduler(alarmManager, context)
                val dataShare =
                    dailyPrayerDB.Name + "||" + dailyPrayerDB.Time + "||" + diffTime + "||" + "$startTime:00" + "||" + "$endTime:00"

                Log.e(
                    "WorkManger",
                    " (getDiffMinute(currentTime, startTime) - timeGapInEveryWhere > 0)+  ${
                        (getDiffMinute(
                            currentTime,
                            startTime
                        ) - timeGapInEveryWhere)
                    } startTime + $startTime (Helper.getDiffSeconds(currentTime, )  ${
                        (Helper.getDiffSeconds(
                            getCurrentTimeWithSeconds(),
                            "$startTime:00"
                        ))
                    }    +"
                )

                if ((Helper.getDiffSeconds(
                        getCurrentTimeWithSeconds(), "$startTime:00"
                    ) > 0)
                )
                    alarmsScheduler.setUpRTCAlarm(
                        dataShare,
                        System.currentTimeMillis() + ((getDiffMinute(currentTime, startTime) - timeGapInEveryWhere) * 1000 * 60)
                    )
                else {

                    CoroutineScope(Dispatchers.IO).launch {
                        Helper.setIsWorkMangerRunning(context,false)

                        delay(1000 * 60)
                        startWorker()
                    }
                }


//                val alarmsScheduler = AlarmsScheduler(alarmManager,context)
//                 val dataShare  = dailyPrayerDB.Name + "||" + dailyPrayerDB.Time + "||" + diffTime +"||" + "15:48:10" +"||" + "15:48:40"
//
//                Log.e("WorkManger", "diffTime $diffTime dataShare $dataShare ")
//
//
//                if( (Helper.getDiffSeconds(currentTime, startTime))  > 0)
//                alarmsScheduler.setUpRTCAlarm(dataShare,  1000*30)


            } else {
                Log.e("WorkManger", " still not found db")
            }
        }

    }

    fun pendingIntentUpdateCurrentFlag(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    }

    companion object {
        const val prayerNameID = "PrayerNameID"
        const val prayerTimeID = "PrayerTimeID"
        const val diffTimeID = "DiffTimeID"
        const val startTimeID = "StartTimeID"
        const val endTimeID = "EndTimeID"


        const val timeGapInEveryWhere = 5

        const val titleNotificationID = "TitleNotificationID"
        const val subTitleNotificationID = "SubTitleNotificationID"
        const val previousModeID = "PreviousModeID"

        const val workMangerTAGID = "WorkMangerID"


    }
}