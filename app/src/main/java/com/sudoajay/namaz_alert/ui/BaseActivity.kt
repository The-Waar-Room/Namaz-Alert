package com.sudoajay.namaz_alert.ui

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.data.repository.WebScrappingGoogle
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask
import com.sudoajay.namaz_alert.ui.bottomSheet.DoNotDisturbPermissionBottomSheet
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
import com.sudoajay.namaz_alert.ui.notification.AlertNotification
import com.sudoajay.namaz_alert.ui.notification.NotificationChannels
import com.sudoajay.namaz_alert.util.Helper
import com.sudoajay.namaz_alert.util.LocalizationUtil.changeLocale
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var protoManager: ProtoManager


    @Inject
    lateinit var workManger: WorkMangerForTask

    @Inject
    lateinit var notDisturbPermissionBottomSheet: DoNotDisturbPermissionBottomSheet


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setSystemDefaultOn()

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


    }


}