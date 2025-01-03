package com.sudoajay.triumph_path.ui.feedbackAndHelp

import android.app.Activity
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import android.util.DisplayMetrics
import com.sudoajay.triumph_path.R
import com.sudoajay.triumph_path.util.ConnectivityType
import com.sudoajay.triumph_path.util.FileSize
import com.sudoajay.triumph_path.util.Helper.Companion.getPackageInfoCompat


import java.io.IOException
import java.util.*

class SystemInfo(private var activity: Activity) {


    fun getAppInfo(): PackageInfo {
        return activity.packageManager.getPackageInfoCompat(
            activity.packageName, PackageManager.GET_META_DATA
        )
    }

    fun getInfo(str: String): String {
        when (str) {
            "MANUFACTURER" -> return Build.MANUFACTURER.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }
            "MODEL" -> return Build.MODEL
            "PRODUCT" -> return Build.PRODUCT
            "SDK_INT" -> return Build.VERSION.SDK_INT.toString()
        }
        return ""
    }

    fun getLanguage(): String {
        return Locale.getDefault().displayLanguage
    }

    fun getHeapTotalSize(): Long {
        return Debug.getNativeHeapSize()
    }

    fun getHeapFreeSize(): Long {
        return Debug.getNativeHeapFreeSize()
    }

    fun getScreenSize(): DisplayMetrics {
        return activity.resources.displayMetrics
    }

    fun createTextForEmail(): StringBuilder {
        val writer = StringBuilder("")
        try {


            writer.append("\n\n\n ===== ${activity.getString(R.string.system_info_text)} =====")
            writer.append(
                "\n\n ${activity.getString(R.string.device_text)} : " + getInfo("MANUFACTURER") + "  " +
                        getInfo("MODEL") + " (" + getInfo("PRODUCT") + ")"
            )
            writer.append("\n ${activity.getString(R.string.os_api_level_text)} : " + getInfo("SDK_INT"))
            writer.append("\n ${activity.getString(R.string.app_version_text)} : " + getAppInfo().versionName)
            writer.append("\n ${activity.getString(R.string.language_text)} : " + getLanguage())
            writer.append(
                "\n ${activity.getString(R.string.total_memory_text)} : " + FileSize.convertIt(
                    getHeapTotalSize()
                )
            )
            writer.append(
                "\n ${activity.getString(R.string.free_memory_text)} : " + FileSize.convertIt(
                    getHeapFreeSize()
                )
            )
            writer.append(
                " \n ${activity.getString(R.string.screen_Text)} : " + getScreenSize().heightPixels.toString()
                        + " x " + getScreenSize().widthPixels.toString()
            )
            writer.append(
                "\n ${activity.getString(R.string.network_type_text)} : " + ConnectivityType.getNetworkProvider(
                    activity
                )
            )
        } catch (_: IOException) {

        }
        return writer
    }


}