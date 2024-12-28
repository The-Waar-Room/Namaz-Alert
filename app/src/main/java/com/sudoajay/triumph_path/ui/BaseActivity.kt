package com.sudoajay.triumph_path.ui

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sudoajay.triumph_path.data.proto.ProtoManager
import com.sudoajay.triumph_path.ui.background.AlarmMangerForTask
import com.sudoajay.triumph_path.ui.bottomSheet.DoNotDisturbPermissionBottomSheet
import com.sudoajay.triumph_path.util.Helper
import com.sudoajay.triumph_path.util.LocalizationUtil.changeLocale
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.Manifest
import androidx.annotation.RequiresApi

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var protoManager: ProtoManager


    @Inject
    lateinit var workManger: AlarmMangerForTask

    @Inject
    lateinit var notDisturbPermissionBottomSheet: DoNotDisturbPermissionBottomSheet



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setSystemDefaultOn()

        val previousMode = Helper.getPreviousPhoneMode(applicationContext)

        // Check and request the USE_EXACT_ALARM permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.USE_EXACT_ALARM), REQUEST_CODE)
            } else {
                // Permission already granted
                scheduleAlarm()
            }
        }

        Log.e("BaseActivity", Helper.getTomorrowDate() + " Here we go   +   previousMode $previousMode Helper.getPhoneMode(previousMode)  ${ Helper.getPhoneMode(previousMode)}")





    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, schedule your alarm
                scheduleAlarm()
            } else {
                // Permission denied, handle accordingly
                showPermissionDeniedMessage()
            }
        }
    }

    private fun scheduleAlarm() {
        // Logic to schedule your alarm
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(this, "Exact Alarm Permission Denied", Toast.LENGTH_SHORT).show()
    }






    private fun setSystemDefaultOn() {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )
    }




    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        overrideConfiguration?.let {
            val uiMode = it.uiMode
            it.setTo(baseContext.resources.configuration)
            it.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(ContextWrapper(context.changeLocale(Helper.getLanguage(context))))

    }




    companion object {
        fun isSystemDefaultOn(resources: Resources): Boolean {
            return resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }

        const val openMainActivityID = "OpenMainActivityID"
        const val openSelectLanguageID = "OpenSelectLanguage"
        const val openSelectNotificationSoundID = "OpenSelectNotificationSound"
        const val messageType = "MessageType"
        const val vibrateModeID = "VibrateMode"
        const val settingShortcutId = "Setting"
        const val notificationSoundID = "NotificationSound"
        const val nextPrayerID = "NextPrayer"

        private const val REQUEST_CODE = 101

    }


}