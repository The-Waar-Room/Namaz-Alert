package com.sudoajay.namaz_alert.ui

import android.app.Activity
import android.view.WindowManager
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.util.Toaster
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

open class BaseFragment :Fragment() {
    @Inject
    lateinit var protoManager: ProtoManager



    fun Activity.changeStatusBarColor(color: Int, isLight: Boolean) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isLight
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

    }
}