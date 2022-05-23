package com.sudoajay.namaz_alert.ui

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.data.repository.WebScrappingGoogle
import com.sudoajay.namaz_alert.ui.background.WorkMangerForTask
import com.sudoajay.namaz_alert.ui.notification.AlertNotification
import com.sudoajay.namaz_alert.ui.notification.NotificationChannels
import com.sudoajay.namaz_alert.util.Helper
import com.sudoajay.namaz_alert.util.Helper.Companion.throwToaster
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSIONS_REQUEST_LOCATION = 1


    @Inject
    lateinit var protoManager: ProtoManager

    @Inject
    lateinit var alertNotification: AlertNotification

    @Inject
    lateinit var workManger: WorkMangerForTask

    @Inject
    lateinit var webScrappingGoogle: WebScrappingGoogle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()

        setSystemDefaultOn()

        webScrappingGoogle.getData()
//

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannels.notificationOnCreate(applicationContext)
        }

        val nm: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !nm.isNotificationPolicyAccessGranted) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        }

    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSIONS_REQUEST_LOCATION
            )
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                CoroutineScope(Dispatchers.IO).launch {
                    protoManager.setLatitudeLongitude(location?.latitude.toString(), location?.longitude.toString())
                }
                throwToaster(applicationContext,"Latitude: ${location?.latitude}, Longitude: ${location?.longitude}")
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                throwToaster(applicationContext,getString(R.string.you_need_to_allow_location_text))
                return
            }
        }
    }



    private fun setSystemDefaultOn() {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        )
    }


    private fun getDataFromProtoDatastore() {
        Helper.setWorkMangerRunning(protoManager,applicationContext, false)
        CoroutineScope(Dispatchers.IO).launch {

            if (protoManager.fetchInitialPreferences().setPhoneMode == "") {
                protoManager.setDefaultValue()
                protoManager.setPreviousMode(Helper.getRingerMode(applicationContext))
            }
            Log.e(
                "BaseActivity",
                "isWorkMangerRunning - sad  ${protoManager.fetchInitialPreferences().isWorkMangerRunning}"
            )

            // Get the sort order from preferences and convert it to a [SortOrder] object
            if (!protoManager.fetchInitialPreferences().isWorkMangerRunning)
                workManger.startWorker()
        }
    }

    companion object {
        fun isSystemDefaultOn(resources: Resources): Boolean {
            return resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }

        const val openMainActivityID = "OpenMainActivityID"
        const val messageType = "MessageType"
        const val settingShortcutId = "setting"
        const val phoneModeShortcutId = "phoneMode"
        const val receiverId = "ReceiverId"
        const val notificationCancelReceiver ="NotificationCancelReceiver"


    }


}