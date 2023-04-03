package com.sudoajay.namaz_alert.ui.setting

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.model.MessageType
import com.sudoajay.namaz_alert.ui.BaseActivity
import com.sudoajay.namaz_alert.ui.bottomSheet.DoNotDisturbPermissionBottomSheet
import com.sudoajay.namaz_alert.ui.feedbackAndHelp.SendFeedbackAndHelp
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
import com.sudoajay.namaz_alert.ui.rateUs.RateUsDialog
import com.sudoajay.namaz_alert.util.DeleteCache
import com.sudoajay.namaz_alert.util.Helper
import com.sudoajay.namaz_alert.util.Toaster
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {

    private var isDarkTheme: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isDarkTheme = isSystemDefaultOn(resources)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                false

        }
        setContentView(R.layout.activity_settings)
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.settings,
                SettingsFragment()
            )
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
               onBack()
            }
        })
    }



    @AndroidEntryPoint
    class SettingsFragment : PreferenceFragmentCompat() {



        @Inject
        lateinit var notDisturbPermissionBottomSheet: DoNotDisturbPermissionBottomSheet

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.setting_preferences, rootKey)

            val phoneMode = findPreference("phoneMode") as ListPreference?
            phoneMode!!.setOnPreferenceChangeListener { _, newValue ->
               Helper.setPhoneMode(requireContext(), newValue as String)
                true
            }
            val notificationSound =
                findPreference("notificationSound") as Preference?
            notificationSound!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                openSelectNotificationSound()
                true
            }
            val selectLanguage =
                findPreference("changeLanguage") as Preference?
            selectLanguage!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                openSelectLanguage()
                true
            }

            val notificationPermission =
                findPreference("notificationPermission") as Preference?
            notificationPermission!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                if(Helper.notificationPermissionAlreadyGiven(requireContext())) {
                    Toaster.showToast(requireContext(),getString(R.string.permission_already_given_text))
                }else if (Helper.notificationPermissionSupported()){
                    Toaster.showToast(requireContext(),getString(R.string.only_13_plus_permission_issue_text))
                }else{
                    Log.e("BaseActivityTAG","Here showPermissionAskedDrawer")

                    askedNotificationPermission()
                }

                true
            }


            val doNotDisturbPermission =
                findPreference("doNotDisturbPermission") as Preference?
            doNotDisturbPermission!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                if(Helper.doNotDisturbPermissionAlreadyGiven(requireContext())) {
                    Toaster.showToast(requireContext(),getString(R.string.permission_already_given_text))
                }else if (Helper.doNotDisturbPermissionSupported()){
                    Toaster.showToast(requireContext(),getString(R.string.sorry_this_feature_not_supported_text))
                }else{
                    Log.e("BaseActivityTAG","Here showPermissionAskedDrawer")

                    showPermissionAskedDrawer()
                }

                true
            }


            val clearCache =
                findPreference("clearCache") as Preference?
            clearCache!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                DeleteCache.deleteCache(requireContext())
                true
            }

            val privacyPolicy =
                findPreference("privacyPolicy") as Preference?
            privacyPolicy!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                openPrivacyPolicy()
                true
            }

            val sendFeedback =
                findPreference("sendFeedback") as Preference?
            sendFeedback!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                sendFeedback()
                true
            }


            val shareApp =
                findPreference("shareApp") as Preference?
            shareApp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                shareApp()
                true
            }
            val submitReview =
                findPreference("submitReview") as Preference?
            submitReview!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                submitReview()
                true
            }
            val moreApps =
                findPreference("moreApps") as Preference?
            moreApps!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                moreApps()
                true
            }
            val aboutApp =
                findPreference("aboutApp") as Preference?
            aboutApp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                openGithubApp()
                true
            }


        }


        private fun openPrivacyPolicy() {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(getString(R.string.privacy_link_text))
            startActivity(i)
        }


        private fun shareApp() {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, "Link-Share")
            i.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareMessage) + " - git " + getString(R.string.rating_link_text))
            startActivity(Intent.createChooser(i, "Share via"))
        }


        private fun moreApps() {
            val link = "https://play.google.com/store/apps/dev?id=5309601131127361849"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(link)
            startActivity(i)
        }

        private fun openGithubApp() {
            val link = "https://play.google.com/store/apps/dev?id=5309601131127361849"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(link)
            startActivity(i)
        }

        private fun submitReview() {
            val ft = childFragmentManager.beginTransaction()
            val systemInfoDialog = RateUsDialog()
            systemInfoDialog.show(ft, "dialog")
        }

        private fun sendFeedback() {
            startActivity(
                Intent(
                    requireContext(),
                    SendFeedbackAndHelp::class.java
                ).putExtra("MessageType", MessageType.FeedBack.toString())
            )
        }

        private fun openSelectLanguage() {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra(openMainActivityID, openSelectLanguageID)
            startActivity(intent)
        }

        private fun openSelectNotificationSound() {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra(openMainActivityID, openSelectNotificationSoundID)
            startActivity(intent)
        }
        private fun askedNotificationPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&   ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                pushNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

        }
        private fun showPermissionAskedDrawer() {
            notDisturbPermissionBottomSheet.show(
                parentFragmentManager.beginTransaction(),
                notDisturbPermissionBottomSheet.tag)

        }


        private val pushNotificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted)
                Toaster.showToast(requireContext(),getString(R.string.permission_not_granted_by_the_user_text))


        }
    }

     fun onBack() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }


}