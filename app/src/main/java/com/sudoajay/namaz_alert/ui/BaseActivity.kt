package com.sudoajay.namaz_alert.ui

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.WorkManager
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.data.repository.WebScrappingGoogle
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask
import com.sudoajay.namaz_alert.ui.notification.AlertNotification
import com.sudoajay.namaz_alert.ui.notification.NotificationChannels
import com.sudoajay.namaz_alert.util.Helper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var protoManager: ProtoManager

    @Inject
    lateinit var alertNotification: AlertNotification

    @Inject
    lateinit var workManger: WorkMangerForTask

    @Inject
    lateinit var webScrappingGoogle: WebScrappingGoogle

    var notificationRingtone = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webScrappingGoogle.checkEvertTimeIfDataIsUpdated()
        getDataFromProtoDatastore()
        setSystemDefaultOn()



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannels.notificationOnCreate(applicationContext, notificationRingtone)
        }

        val nm: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !nm.isNotificationPolicyAccessGranted) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        }

    }


    private fun setSystemDefaultOn() {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )
    }


    private fun getDataFromProtoDatastore() {
        Helper.setWorkMangerRunning(protoManager, applicationContext, false)
        CoroutineScope(Dispatchers.IO).launch {

            if (protoManager.fetchInitialPreferences().setPhoneMode == "") {
                protoManager.setDefaultValue()
                protoManager.setPreviousMode(Helper.getRingerMode(applicationContext))
            }else{
                notificationRingtone = protoManager.fetchInitialPreferences().notificationRingtone
            }
            Log.e(
                "BaseActivity",
                "isWorkMangerRunning - sad  ${protoManager.fetchInitialPreferences().isWorkMangerRunning}"
            )

            // Get the sort order from preferences and convert it to a [SortOrder] object

            if (!protoManager.fetchInitialPreferences().isWorkMangerRunning) {
                workManger.startWorker()
            }
        }
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
        const val settingShortcutId = "setting"
        const val phoneModeShortcutId = "phoneMode"
        const val receiverId = "ReceiverId"
        const val notificationCancelReceiver = "NotificationCancelReceiver"


    }


}