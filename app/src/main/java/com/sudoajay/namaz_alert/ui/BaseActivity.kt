package com.sudoajay.namaz_alert.ui

import android.content.*
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.ui.background.AlarmMangerForTask
import com.sudoajay.namaz_alert.ui.bottomSheet.DoNotDisturbPermissionBottomSheet
import com.sudoajay.namaz_alert.util.Helper
import com.sudoajay.namaz_alert.util.LocalizationUtil.changeLocale
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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

        Log.e("BaseActivity", Helper.getTomorrowDate() + " Here we go ")


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
        const val phoneModeShortcutId = "phoneMode"
        const val receiverId = "ReceiverId"
        const val notificationCancelReceiver = "NotificationCancelReceiver"
        const val CommandTAG = "COMMAND"


    }


}