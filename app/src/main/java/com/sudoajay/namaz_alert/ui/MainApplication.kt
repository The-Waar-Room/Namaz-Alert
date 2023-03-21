package com.sudoajay.namaz_alert.ui

import android.app.Application
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.data.repository.WebScrappingGoogle
import com.sudoajay.namaz_alert.ui.background.AlarmMangerForTask
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
    lateinit var workManger: AlarmMangerForTask

    var notificationRingtone = 0


    override fun onCreate() {
        super.onCreate()


        webScrappingGoogle.checkEvertTimeIfDataIsUpdated()
        getDataFromProtoDatastore()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
            if(notificationManager.getNotificationChannel(NotificationChannels.UPCOMING_PRAYER_TIME) == null) {
                NotificationChannels.notificationOnCreate(applicationContext)
            }
        }


    }


    private fun getDataFromProtoDatastore() {
        CoroutineScope(Dispatchers.IO).launch {
            val fetch = protoManager.fetchInitialPreferences()
            if (fetch.dhuhrTiming == "") {
                Log.e("WorkManger", " first time ")
                protoManager.setDefaultValue()
                Helper.setLanguage(applicationContext, Helper.getLanguage(applicationContext))

            } else {

                notificationRingtone = Helper.getNotificationRingtone(applicationContext)

                workManger.startWorker()

            }
        }

    }
}