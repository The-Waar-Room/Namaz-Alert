package com.sudoajay.namaz_alert.ui.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.data.proto.ProtoManager
import com.sudoajay.namaz_alert.model.MessageType
import com.sudoajay.namaz_alert.ui.BaseActivity
import com.sudoajay.namaz_alert.ui.bottomSheet.DoNotDisturbPermissionBottomSheet
import com.sudoajay.namaz_alert.ui.feedbackAndHelp.SendFeedbackAndHelp
import com.sudoajay.namaz_alert.ui.mainActivity.MainActivity
import com.sudoajay.namaz_alert.util.DeleteCache
import com.sudoajay.namaz_alert.util.Helper
import com.sudoajay.namaz_alert.util.Toaster
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @AndroidEntryPoint
    class SettingsFragment : PreferenceFragmentCompat() {
        private val ratingLink =
            "https://play.google.com/store/apps/details?id=com.sudoajay.duplication_data"

        @Inject
        lateinit var protoManager: ProtoManager


        @Inject
        lateinit var notDisturbPermissionBottomSheet: DoNotDisturbPermissionBottomSheet

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.setting_preferences, rootKey)



            val phoneMode = findPreference("phoneMode") as ListPreference?
            phoneMode!!.setOnPreferenceChangeListener { _, newValue ->
                lifecycleScope.launch {
                    protoManager.setPhoneMode(newValue as String)
                }
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

            val doNotDisturbPermission =
                findPreference("doNotDisturbPermission") as Preference?
            doNotDisturbPermission!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                Log.e("BaseActivityTAG","Here doNotDisturbPermissionAlreadyGiven  - ${Helper.doNotDisturbPermissionAlreadyGiven(requireContext())}")
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
            val rateUs =
                findPreference("rateUs") as Preference?
            rateUs!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                rateUs()
                true
            }
            val moreApp =
                findPreference("moreApp") as Preference?
            moreApp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                moreApp()
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
            val link = "https://play.google.com/store/apps/dev?id=5309601131127361849"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(link)
            startActivity(i)
        }


        private fun shareApp() {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, "Link-Share")
            i.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareMessage) + " - git " + ratingLink)
            startActivity(Intent.createChooser(i, "Share via"))
        }

        private fun rateUs() {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(ratingLink)
            startActivity(i)
        }

        private fun moreApp() {
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

        private fun showPermissionAskedDrawer() {
            notDisturbPermissionBottomSheet.show(
                parentFragmentManager.beginTransaction(),
                notDisturbPermissionBottomSheet.tag)

        }
    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

}