package com.sudoajay.namaz_alert

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val nm:NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && !nm.isNotificationPolicyAccessGranted){
//            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
//        }
//
//        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        am.ringerMode = AudioManager.RINGER_MODE_SILENT
    }
}
