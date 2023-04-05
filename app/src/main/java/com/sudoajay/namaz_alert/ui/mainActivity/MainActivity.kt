package com.sudoajay.namaz_alert.ui.mainActivity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.data.db.DailyPrayerDatabase
import com.sudoajay.namaz_alert.data.repository.DailyPrayerRepository
import com.sudoajay.namaz_alert.databinding.ActivityMainBinding
import com.sudoajay.namaz_alert.ui.BaseActivity
import com.sudoajay.namaz_alert.ui.BaseFragment
import com.sudoajay.namaz_alert.ui.background.AlarmMangerForTask
import com.sudoajay.namaz_alert.ui.background.AlarmsScheduler
import com.sudoajay.namaz_alert.ui.notification.AlertNotification
import com.sudoajay.namaz_alert.ui.notification.NotificationChannels
import com.sudoajay.namaz_alert.ui.setting.SettingsActivity
import com.sudoajay.namaz_alert.util.Helper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private var isDarkTheme: Boolean = false

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var isPermissionAsked = false
    private lateinit var notificationCompat: NotificationCompat.Builder
    private lateinit var alertNotification: AlertNotification
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var keepSplashOnScreen = true
        val delay = 1000L

        installSplashScreen().setKeepOnScreenCondition { keepSplashOnScreen }
        Handler(Looper.getMainLooper()).postDelayed({ keepSplashOnScreen = false }, delay)

        Log.e(
            "ACTIVITYTAG",
            " i am heree  +   ${intent.getStringExtra(AlarmMangerForTask.prayerNameID)}   ${
                intent.getStringExtra(AlarmMangerForTask.prayerTimeID)
            } ${intent.getStringExtra(AlarmsScheduler.DATA_SHARE_ID)}    "
        )



        isDarkTheme = isSystemDefaultOn(resources)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme) {
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                    true
            }
        }
        Log.e("WorkManger", " i am here" + intent.action)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        alertNotification = AlertNotification(context = applicationContext)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (!intent.action.isNullOrEmpty()) {
            when (intent.action.toString()) {
                vibrateModeID -> openSetting()
                notificationSoundID -> openSelectRingtone()
                nextPrayerID -> openNextPrayer()
                else -> {}
            }

        }



        if (intent.getStringExtra(openMainActivityID) == settingShortcutId) {
            openSetting()
        } else if (intent.getStringExtra(openMainActivityID) == openSelectLanguageID) {
            openSelectLanguage()
        } else if (intent.getStringExtra(openMainActivityID) == openSelectNotificationSoundID) {
            openSelectRingtone()
        } else if (intent.getStringExtra(AlarmMangerForTask.prayerNameID)?.isNotEmpty() == true) {
            openSpecificEditPrayer()
            if (intent.getStringExtra(AlarmsScheduler.DATA_SHARE_ID)?.isNotEmpty() == true)
                openNewNotification()
        }

        isPermissionAsked = Helper.IsPermissionAsked(applicationContext)
        showPermissionAskedDrawer()


    }


    private fun openSpecificEditPrayer(prayerName: String? = null, prayerTime: String? = null) {
        navController.navigate(
            R.id.action_homeFragment_to_editDailyPrayerFragment,
            bundleOf(
                BaseFragment.editDailyPrayerNameKey to if (prayerName.isNullOrEmpty())
                    intent.getStringExtra(AlarmMangerForTask.prayerNameID) else prayerName,
                BaseFragment.editDailyPrayerTimeKey to if (prayerTime.isNullOrEmpty()) intent.getStringExtra(
                    AlarmMangerForTask.prayerTimeID
                )
                else prayerTime
            )
        )
    }

    private fun openSetting() {
        val intent = Intent(applicationContext, SettingsActivity::class.java)
        startActivity(intent)
    }


    private fun openSelectLanguage() {
        navController.navigate(
            R.id.action_homeFragment_to_selectLanguageFragment
        )
    }

    private fun openSelectRingtone() {
        navController.navigate(
            R.id.action_homeFragment_to_selectRingtoneFragment
        )
    }

    private fun openNextPrayer() {
        CoroutineScope(Dispatchers.IO).launch {
            val dailyPrayerRepository =
                DailyPrayerRepository(
                    DailyPrayerDatabase.getDatabase(applicationContext).dailyPrayerDoa()
                )
            val currentTime = Helper.getCurrentTime()
            val dailyPrayerDB =
                dailyPrayerRepository.getNextTime(
                    Helper.getTodayDate(),
                    Helper.getTomorrowDate(),
                    currentTime
                )
            withContext(Dispatchers.Main) {
                openSpecificEditPrayer(dailyPrayerDB.Name, dailyPrayerDB.Time)
            }
        }
    }

    private fun showPermissionAskedDrawer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isPermissionAsked && !(Helper.doNotDisturbPermissionAlreadyGiven(
                applicationContext
            ))
        )
            notDisturbPermissionBottomSheet.show(
                supportFragmentManager.beginTransaction(),
                notDisturbPermissionBottomSheet.tag
            )

    }


    private fun openNewNotification() {
        notificationManager.cancel(AlertNotification.NOTIFICATION_ALERT_STATE)

        val phoneMode = Helper.getPhoneMode(context = applicationContext)
        val notificationRingtone = Helper.getNotificationRingtone(applicationContext)
        startNotificationAlert(
            phoneMode,
            notificationRingtone,
            intent.getStringExtra(AlarmsScheduler.DATA_SHARE_ID).toString()
        )
    }

    private fun startNotificationAlert(
        phoneMode: String,
        notificationRingtone: Int, dataShare: String
    ) {
        createNotificationAlert(dataShare, notificationRingtone)
        alertNotification.notifyCompat(
            phoneMode,
            notificationRingtone, notificationCompat, dataShare, notificationManager
        )
    }


    private fun createNotificationAlert(
        dataShare: String, notificationRingtone: Int
    ) {
        val arr = dataShare.split("||")

        notificationCompat =
            NotificationCompat.Builder(
                applicationContext,
                if (notificationRingtone == 0) NotificationChannels.ALERT_DEFAULT_PRAYER_TIME else NotificationChannels.ALERT_SOUND_PRAYER_TIME
            )
        notificationCompat.setSmallIcon(R.mipmap.ic_launcher)
        notificationCompat.setContentIntent(
            createPendingIntent(
                applicationContext,
                arr[0],
                arr[1],
                dataShare
            )
        )

    }

    private fun createPendingIntent(
        context: Context,
        prayerName: String,
        prayerTime: String,
        dataShare: String? = null

    ): PendingIntent? {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(AlarmMangerForTask.prayerNameID, prayerName)
        intent.putExtra(AlarmMangerForTask.prayerTimeID, prayerTime)
        intent.putExtra(AlarmsScheduler.DATA_SHARE_ID, dataShare)

        return PendingIntent.getActivity(
            context, AlertNotification.NOTIFICATION_ALERT_STATE, intent,
            PendingIntent.FLAG_MUTABLE
        )
    }


}
