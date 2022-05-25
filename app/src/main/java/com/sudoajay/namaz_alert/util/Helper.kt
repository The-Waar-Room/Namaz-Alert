package com.sudoajay.namaz_alert.util

import android.content.Context
import android.media.AudioManager
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.data.repository.DailyPrayerRepository
import com.sudoajay.namaz_alert.ui.BaseFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Helper {

    companion object {

        fun doesDatabaseExist(context: Context, repository: DailyPrayerRepository): Boolean {
            val dbFile: File = context.getDatabasePath("DailyPrayerTable_database")
            return dbFile.exists() && repository.getCount() > 0
        }

        fun throwToaster( context: Context,value: String?) {
            Toaster.showToast(context, value ?: "")
        }
        fun setWorkMangerRunning(
            protoManager: ProtoManager?,
            context: Context,
            isRunning: Boolean
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                protoManager ?: ProtoManager(context).setIsWorkMangerRunning(isRunning)
            }

        }

        fun setWorkMangerCancel(protoManager: ProtoManager?,context: Context, isRunning: Boolean) {
            CoroutineScope(Dispatchers.IO).launch {
                protoManager ?: ProtoManager(context).setIsWorkMangerCancel(isRunning)
            }
        }


        fun getPhoneMode(phoneMode: String): Int {
            return when (phoneMode) {
                PhoneMode.Normal.toString() -> AudioManager.RINGER_MODE_NORMAL
                PhoneMode.Vibrate.toString() -> AudioManager.RINGER_MODE_VIBRATE
                else -> AudioManager.RINGER_MODE_SILENT

            }

        }


        fun getRingerMode(context: Context): String {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            return when (am.ringerMode) {
                0 -> PhoneMode.Silent.toString()
                1 -> PhoneMode.Vibrate.toString()
                else -> PhoneMode.Normal.toString()
            }
        }


        fun convertTo12Hours(militaryTime: String): String? {
            //in => "14:00"
            //out => "02:00 PM"
            val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = inputFormat.parse(militaryTime)
            return date?.let { outputFormat.format(it) }
        }

        fun getAMOrPM(time: String): String {
            val hour = time.split(":")[0]
            return if (hour.toInt() < 12) BaseFragment.amText else BaseFragment.pmText
        }

        fun getTodayDate(): String {
            val todayDate = Date() // sets date
            val formatDate = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
            return formatDate.format(todayDate)
        }

        fun getCurrentTime(): String {
            val todayDate = Date() // sets date
            val formatDate = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            return formatDate.format(todayDate)
        }


        suspend fun getPrayerGapTime(prayerName: String, protoManager: ProtoManager): String {
            return when (prayerName) {
                BaseFragment.fajrName -> protoManager.fetchInitialPreferences().fajrTiming
                BaseFragment.dhuhrName -> protoManager.fetchInitialPreferences().dhuhrTiming
                BaseFragment.asrName -> protoManager.fetchInitialPreferences().asrTiming
                BaseFragment.maghribName -> protoManager.fetchInitialPreferences().maghribTiming
                else -> protoManager.fetchInitialPreferences().ishaTiming
            }
        }

        fun getMeIncrementTime(time: String, minuteIncrement: Int): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            val timeArray = time.split(":")
            val newDate = Calendar.getInstance()
            newDate.set(Calendar.HOUR_OF_DAY, timeArray[0].toInt())
            newDate.set(Calendar.MINUTE, timeArray[1].toInt())
            newDate.add(Calendar.MINUTE, minuteIncrement)
            return sdf.format(newDate.time).toString()
        }

        fun getDiffMinute(currentTime: String, nextTime: String): Long {
            val currentDate = getDateFromString(currentTime)
            val nextDate = getDateFromString(nextTime)
            val diff: Long = nextDate?.time!! - currentDate?.time!!

            val diffMinutes = diff / (60 * 1000) % 60
            val diffHours = diff / (60 * 60 * 1000) % 24

            return diffMinutes + (diffHours * 60)
        }

        private fun getDateFromString(time: String): Date? {
            val formatter: DateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            return formatter.parse(time)
        }
    }
}