package com.sudoajay.namaz_alert.ui

import android.app.Application
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.data.repository.WebScrappingGoogle
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask
import com.sudoajay.namaz_alert.ui.notification.NotificationChannels
import com.sudoajay.namaz_alert.util.Helper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {
    @Inject
    lateinit var webScrappingGoogle: WebScrappingGoogle

    @Inject
    lateinit var protoManager: ProtoManager

    @Inject
    lateinit var workManger: WorkMangerForTask


    var notificationRingtone = 0

    override fun onCreate() {
        super.onCreate()
        webScrappingGoogle.checkEvertTimeIfDataIsUpdated()
        getDataFromProtoDatastore()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannels.notificationOnCreate(applicationContext, notificationRingtone)
        }
    }


    private fun getDataFromProtoDatastore() {
        Helper.setWorkMangerRunning(protoManager, applicationContext, false)
        CoroutineScope(Dispatchers.IO).launch {
            val fetch = protoManager.fetchInitialPreferences()
            Log.e("WorkManger", " setPhoneMode ${protoManager.fetchInitialPreferences().previousPhoneMode}  asdasd ${protoManager.fetchInitialPreferences().setPhoneMode}")
            if (fetch.setPhoneMode == "") {
                Log.e("WorkManger", " first time ")
                protoManager.setDefaultValue()
                protoManager.setPreviousMode(Helper.getRingerMode(applicationContext))
                Helper.setLanguage(applicationContext, Helper.getLanguage(applicationContext))

            } else {
                notificationRingtone = fetch.notificationRingtone
                if (!fetch.isWorkMangerRunning) {
                    workManger.startWorker()
                }
            }
        }

    }
}