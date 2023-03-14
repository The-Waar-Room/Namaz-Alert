package com.sudoajay.namaz_alert.util

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Build
import android.util.Log
import androidx.preference.PreferenceManager
import com.sudoajay.namaz_alert.R
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


        fun getLanguage(context: Context): String {
            return PreferenceManager
                .getDefaultSharedPreferences(context).getString("changeLanguageValue", getLocalLanguage(context))
                .toString()
        }

        fun setLanguage(context: Context, value:String){
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putString("changeLanguageValue", value)
            editor.apply()
        }

        fun getPhoneMode(context: Context): String {
            return PreferenceManager
                .getDefaultSharedPreferences(context).getString("PhoneMode", PhoneMode.Vibrate.toString())
                .toString()
        }

        fun setPhoneMode(context: Context, value:String){
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putString("PhoneMode", value)
            editor.apply()
        }

        fun getPreviousPhoneMode(context: Context): String {
            return PreferenceManager
                .getDefaultSharedPreferences(context).getString("PreviousPhoneMode", PhoneMode.Normal.toString())
                .toString()
        }

        fun setPreviousPhoneMode(context: Context, value:String){
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putString("PreviousPhoneMode", value)
            editor.apply()
        }

        fun isWorkMangerRunning(context: Context): Boolean {
            return PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean("IsWorkMangerRunning", false)
        }

        fun setIsWorkMangerRunning(context: Context, value:Boolean){
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putBoolean("IsWorkMangerRunning", value)
            editor.apply()
        }

        fun IsPermissionAsked(context: Context): Boolean {
            return PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean("IsPermissionAsked", false)
        }

        fun setIsPermissionAsked(context: Context, value:Boolean){
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putBoolean("IsPermissionAsked", value)
            editor.apply()
        }

        fun getNotificationRingtone(context: Context): Int {
            return PreferenceManager
                .getDefaultSharedPreferences(context).getInt("NotificationRingtone", 0)
        }

        fun setNotificationRingtone(context: Context, value:Int){
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putInt("NotificationRingtone", value)
            editor.apply()
        }

        fun setActionTime(context: Context,type: String,value :String){
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putString(type, value)
            editor.apply()
        }

        private fun getLocalLanguage(context: Context): String {
            val lang = Locale.getDefault().language
            val array = context.resources.getStringArray(R.array.languageValues)

            return if (lang in array) lang else "en"
        }
        fun doNotDisturbPermissionAlreadyGiven(context: Context):Boolean{
            val nm: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
           return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && nm.isNotificationPolicyAccessGranted
        }

        fun setRingerMode(context: Context ,ringerMode:Int){
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.ringerMode = ringerMode
        }

        fun doNotDisturbPermissionSupported():Boolean{
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
        }

        fun doesDatabaseExist(context: Context, repository: DailyPrayerRepository): Boolean {
            val dbFile: File = context.getDatabasePath("DailyPrayerTable_database")
            return dbFile.exists() && repository.getCount() > 0
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

        private fun getAMOrPM(context: Context, time: String): String {
            val hour = time.split(":")[0]
            Log.e("NewGapTime" , " new time getAMOrPM  ${time}")
            return if (hour.toInt() < 12) context.getString(R.string.am_text) else context.getString(R.string.pm_text)
        }

        fun convertTo12Hr(context: Context, time:String): String {
            val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
            val date = inputFormat.parse(time)
            return date?.let { outputFormat.format(it) } + " ${getAMOrPM(context,time)}"
        }

        fun getTodayDate(): String {
            val todayDate = Date() // sets date
            val formatDate = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
            return formatDate.format(todayDate)
        }

        fun getTomorrowDate(): String {
            val gc = GregorianCalendar()
            gc.add(Calendar.DATE, 1)
            val formatDate = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
            return formatDate.format(gc.time).toString()
        }
        fun getCurrentTime(): String {
            val todayDate = Date() // sets date
            val formatDate = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            return formatDate.format(todayDate)
        }
        fun getCurrentTimeWithSeconds(): String {
            val todayDate = Date() // sets date
            val formatDate = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
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
            val newDate = GregorianCalendar()
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


        fun getDiffSeconds(currentTime: String, nextTime: String): Long {
            val currentDate = getDateSecondFromString(currentTime)
            val nextDate = getDateSecondFromString(nextTime)
            val diff: Long = nextDate?.time!! - currentDate?.time!!

            val diffSeconds = diff / (1000) % 60
            val diffMinutes = diff / (60 * 1000) % 60
            val diffHours = diff / (60 * 60 * 1000) % 24

            return diffSeconds+(diffMinutes*60) + (diffHours * 60*60)
        }

        private fun getDateSecondFromString(time: String): Date? {
            val formatter: DateFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
            return formatter.parse(time)
        }
    }
}