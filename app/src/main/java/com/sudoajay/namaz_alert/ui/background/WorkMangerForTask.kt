package com.sudoajay.namaz_alert.ui.background

import android.content.Context
import android.util.Log
import androidx.work.*
import com.sudoajay.namaz_alert.data.db.DailyPrayerDatabase
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.data.repository.DailyPrayerRepository
import com.sudoajay.namaz_alert.util.Helper.Companion.getCurrentTime
import com.sudoajay.namaz_alert.util.Helper.Companion.getDiffMinute
import com.sudoajay.namaz_alert.util.Helper.Companion.getMeIncrementTime
import com.sudoajay.namaz_alert.util.Helper.Companion.getPrayerGapTime
import com.sudoajay.namaz_alert.util.Helper.Companion.getRingerMode
import com.sudoajay.namaz_alert.util.Helper.Companion.getTodayDate
import com.sudoajay.namaz_alert.util.Helper.Companion.prayerGapTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class WorkMangerForTask @Inject constructor(var context: Context) {

    private lateinit var protoManager: ProtoManager
    private var dailyPrayerDoa = DailyPrayerDatabase.getDatabase(context).dailyPrayerDoa()
    private lateinit var dailyPrayerRepository: DailyPrayerRepository

    suspend fun startWorker() {

        protoManager = ProtoManager(context)
        //        Creating Object and Initialization
        dailyPrayerRepository = DailyPrayerRepository(dailyPrayerDoa)
        val currentTime = getCurrentTime()
        val dailyPrayerDB = dailyPrayerRepository.getNextTime(getTodayDate(), currentTime)
        var phoneMode = ""
        val previousMode = getRingerMode(context)

        val waitFor = CoroutineScope(Dispatchers.IO).async {
            getPrayerGapTime(dailyPrayerDB.Name, protoManager)
            Log.e("WorkManger", "prayerGapTime sas - $prayerGapTime ")
            protoManager.setPreviousMode(previousMode)
            phoneMode = getPhoneMode()
            return@async phoneMode
        }
        waitFor.await()

        Log.e("WorkManger", "prayerGapTime - $prayerGapTime ")
        val arrayIncrement = prayerGapTime.split(":")
        val beforeTime = getMeIncrementTime(dailyPrayerDB.Time, arrayIncrement[0].toInt())
        val afterTime = getMeIncrementTime(beforeTime, arrayIncrement[1].toInt())


        val alertData = workDataOf(
            prayerNameID to dailyPrayerDB.Name,
            prayerTimeID to dailyPrayerDB.Time,
            phoneModeID to phoneMode,
            beforeTimeID to beforeTime,
            afterTimeID to afterTime,
            previousModeID to previousMode
        )

        val finishData = workDataOf(
            titleNotificationID to "You Have Completed Your ${dailyPrayerDB.Name} namaz prayer",
            subTitleNotificationID to "Now your phone is set to previous mode : Normal mode",
            previousModeID to previousMode

        )

        val workManager = WorkManager.getInstance(context)

        val constraints = Constraints.Builder()

        val alertOneTimeWorkRequest = OneTimeWorkRequestBuilder<WorkMangerAlertNotification>()
//            .setInitialDelay(getDiffMinute(currentTime,beforeTime), TimeUnit.MINUTES)
            .addTag(alertTAGID)
            .setInputData(alertData)
            .setConstraints(constraints.build())
            .build()


        val finishOneTimeRequest = OneTimeWorkRequestBuilder<WorkMangerFinishNotification>()
//            .setInitialDelay(getDiffMinute(currentTime,beforeTime), TimeUnit.MINUTES)
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

    }

    private suspend fun getPhoneMode(): String {
        return protoManager.fetchInitialPreferences().setPhoneMode
    }



    companion object {
        const val prayerNameID = "PrayerNameID"
        const val prayerTimeID = "PrayerTimeID"
        const val phoneModeID = "PhoneModeID"
        const val beforeTimeID = "BeforeTimeID"
        const val afterTimeID = "AfterTimeID"
        const val titleNotificationID = "TitleNotificationID"
        const val subTitleNotificationID = "SubTitleNotificationID"
        const val previousModeID ="PreviousModeID"

        const val alertTAGID = "WorkMangerAlertPrayerID"
        const val finishTAGID = "WorkMangerFinishPrayerID"

        const val cancelTAGID = "WorkMangerCancelPrayerID"


    }
}