package com.sudoajay.triumph_path.ui

import android.app.Application
import android.app.NotificationManager
import android.os.Build
import com.sudoajay.triumph_path.data.proto.ProtoManager
import com.sudoajay.triumph_path.data.repository.WebScrappingGoogle
import com.sudoajay.triumph_path.ui.background.AlarmMangerForTask
import com.sudoajay.triumph_path.ui.notification.NotificationChannels
import com.sudoajay.triumph_path.util.Helper
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
    lateinit var workManger: AlarmMangerForTask


    override fun onCreate() {
        super.onCreate()
        getDataFromProtoDatastore()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(NotificationManager::class.java) as NotificationManager
            if (notificationManager.getNotificationChannel(NotificationChannels.UPCOMING_PRAYER_TIME) == null) {
                NotificationChannels.notificationOnCreate(applicationContext)
            }
        }


    }


    private fun getDataFromProtoDatastore() {
        CoroutineScope(Dispatchers.IO).launch {
            webScrappingGoogle.checkEvertTimeIfDataIsUpdated()
            val fetch = protoManager.fetchInitialPreferences()
            if (fetch.dhuhrTiming == "") {
                protoManager.setDefaultValue()
                Helper.setLanguage(applicationContext, Helper.getLanguage(applicationContext))

            } else {
                workManger.startWorker()

            }
        }

    }
}