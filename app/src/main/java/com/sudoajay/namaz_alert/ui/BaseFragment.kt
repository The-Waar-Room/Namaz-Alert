package com.sudoajay.namaz_alert.ui

import android.app.Activity
import android.view.WindowManager
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.ui.background.AlarmMangerForTask
import com.sudoajay.namaz_alert.util.Toaster
import javax.inject.Inject


open class BaseFragment : Fragment() {
    @Inject
    lateinit var protoManager: ProtoManager

    @Inject
    lateinit var workManger: AlarmMangerForTask

    fun Activity.changeStatusBarColor(color: Int, isLight: Boolean) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isLight


    }


    fun throwToaster(value: String?) {
        Toaster.showToast(requireContext(), value ?: "")
    }


    companion object {
        var fajrName = "Fajr"
        var dhuhrName = "Dhuhr"
        var asrName = "Asr"
        var maghribName = "Maghrib"
        var ishaName = "Isha"
        var editDailyPrayerNameKey = "prayerName"
        var editDailyPrayerTimeKey = "prayerTime"

    }
}