package com.sudoajay.namaz_alert.ui

import android.app.Activity
import android.view.WindowManager
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.util.Toaster
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

open class BaseFragment :Fragment() {

    @Inject
    lateinit var protoManager: ProtoManager
    var prayerGapTime = ""

    fun Activity.changeStatusBarColor(color: Int, isLight: Boolean) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isLight
    }

    fun setUpNormalStatusBar(){

//        requireActivity().changeStatusBarColor(
//            ContextCompat.getColor(
//                requireContext(),
//                R.color.statusBarColor
//            ), !(isSystemDefaultOn(resources))
//        )
    }



    suspend fun getPrayerGapTime(prayerName:String ){
        prayerGapTime = when (prayerName) {
            fajrName -> protoManager.fetchInitialPreferences().fajrTiming
            dhuhrName -> protoManager.fetchInitialPreferences().dhuhrTiming
            asrName -> protoManager.fetchInitialPreferences().asrTiming
            maghribName -> protoManager.fetchInitialPreferences().maghribTiming
            else ->protoManager.fetchInitialPreferences().ishaTiming
        }
    }

    fun throwToaster(value: String?) {
        Toaster.showToast(requireContext(), value ?: "")
    }




    companion object{
        var fajrName = "Fajr"
        var dhuhrName = "Dhuhr"
        var asrName = "Asr"
        var maghribName = "Maghrib"
        var ishaName = "Isha"
        var amText ="am"
        var pmText ="pm"
        var editDailyPrayerNameKey = "prayerName"
        var editDailyPrayerTimeKey = "prayerTime"


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
            return if (hour.toInt() < 12) amText else pmText
        }
    }
}