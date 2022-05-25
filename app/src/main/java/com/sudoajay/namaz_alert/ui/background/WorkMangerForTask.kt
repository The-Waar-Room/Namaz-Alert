package com.sudoajay.namaz_alert.ui.background

import android.R.attr.tag
import android.content.Context
import android.util.Log
import androidx.work.*
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.data.db.DailyPrayerDatabase
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.data.repository.DailyPrayerRepository
import com.sudoajay.namaz_alert.util.Helper.Companion.doesDatabaseExist
import com.sudoajay.namaz_alert.util.Helper.Companion.getCurrentTime
import com.sudoajay.namaz_alert.util.Helper.Companion.getDiffMinute
import com.sudoajay.namaz_alert.util.Helper.Companion.getMeIncrementTime
import com.sudoajay.namaz_alert.util.Helper.Companion.getPrayerGapTime
import com.sudoajay.namaz_alert.util.Helper.Companion.getRingerMode
import com.sudoajay.namaz_alert.util.Helper.Companion.getTodayDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class WorkMangerForTask @Inject constructor(var context: Context) {

    private lateinit var protoManager: ProtoManager
    private var dailyPrayerDoa = DailyPrayerDatabase.getDatabase(context).dailyPrayerDoa()
    private lateinit var dailyPrayerRepository: DailyPrayerRepository
    private var prayerGapTime: String = ""

    suspend fun startWorker() {
        WorkManager.getInstance(context).cancelAllWork()
        protoManager = ProtoManager(context)
        //        Creating Object and Initialization
        dailyPrayerRepository = DailyPrayerRepository(dailyPrayerDoa)
        if (doesDatabaseExist(context, dailyPrayerRepository)) {
            val currentTime = getCurrentTime()
            val dailyPrayerDB = dailyPrayerRepository.getNextTime(getTodayDate(), currentTime)
            var phoneMode = ""

            val waitFor = CoroutineScope(Dispatchers.IO).async {
                prayerGapTime = getPrayerGapTime(dailyPrayerDB.Name, protoManager)
                Log.e("WorkManger", "prayerGapTime sas - $prayerGapTime ")
                phoneMode = getPhoneMode()
                return@async phoneMode
            }
            waitFor.await()

            Log.e("WorkManger", "prayerGapTime - $prayerGapTime ")
            val arrayIncrement = prayerGapTime.split(":")
            val beforeTime = getMeIncrementTime(dailyPrayerDB.Time, arrayIncrement[0].toInt())
            val afterTime = getMeIncrementTime(beforeTime, arrayIncrement[1].toInt())
            val diffTime = arrayIncrement[1]


            val alertData = workDataOf(
                prayerNameID to dailyPrayerDB.Name,
                prayerTimeID to dailyPrayerDB.Time,
                phoneModeID to phoneMode,
                diffTimeID to diffTime
            )

            val finishData = workDataOf(
                titleNotificationID to context.getString(
                    R.string.completed_the_notification_prayer,
                    dailyPrayerDB.Name
                ),
                prayerNameID to dailyPrayerDB.Name,
                prayerTimeID to dailyPrayerDB.Time,
            )

            val workManager = WorkManager.getInstance(context)

            val constraints = Constraints.Builder()

            val alertOneTimeWorkRequest = OneTimeWorkRequestBuilder<WorkMangerAlertNotification>()
//            .setInitialDelay(getDiffMinute(currentTime,beforeTime), TimeUnit.MINUTES)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .addTag(alertTAGID)
                .setInputData(alertData)
                .setConstraints(constraints.build())
                .build()


            val finishOneTimeRequest = OneTimeWorkRequestBuilder<WorkMangerFinishNotification>()
                .setInitialDelay(30, TimeUnit.SECONDS)
                .addTag(finishTAGID)
                .setInputData(finishData)
                .setConstraints(constraints.build())
                .build()


            workManager.beginWith(alertOneTimeWorkRequest).then(finishOneTimeRequest).enqueue()




            Log.e(
                "WorkManger", " let see  - ${dailyPrayerDB.Name}  ," +
                        "${dailyPrayerDB.Time} , $phoneMode , " +
                        "$beforeTime , $afterTime  , ${getDiffMinute(currentTime, beforeTime)}" +
                        " getuuid ${alertOneTimeWorkRequest.id.toString()}"
            )
        } else {
            Log.e("WorkManger", " still not found db")
        }

    }


    private suspend fun getPhoneMode(): String {
        return protoManager.fetchInitialPreferences().setPhoneMode
    }


    companion object {
        const val prayerNameID = "PrayerNameID"
        const val prayerTimeID = "PrayerTimeID"
        const val phoneModeID = "PhoneModeID"
        const val diffTimeID = "DiffTimeID"
        const val titleNotificationID = "TitleNotificationID"
        const val subTitleNotificationID = "SubTitleNotificationID"
        const val previousModeID = "PreviousModeID"

        const val alertTAGID = "WorkMangerAlertPrayerID"
        const val finishTAGID = "WorkMangerFinishPrayerID"

        const val cancelTAGID = "WorkMangerCancelPrayerID"


    }
}