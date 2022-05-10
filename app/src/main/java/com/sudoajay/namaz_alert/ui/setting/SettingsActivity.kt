package com.sudoajay.namaz_alert.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.model.MessageType
import com.sudoajay.namaz_alert.ui.BaseActivity
import com.sudoajay.namaz_alert.ui.feedbackAndHelp.SendFeedbackAndHelp
import com.sudoajay.namaz_alert.util.DeleteCache
import com.sudoajay.namaz_alert.data.proto.ProtoManager
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
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

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.setting_preferences, rootKey)


            val phoneMode = findPreference("phoneMode") as ListPreference?
            phoneMode!!.setOnPreferenceChangeListener { _, newValue ->
                lifecycleScope.launch {
                    protoManager.setPhoneMode(newValue as String)
                }
                true
            }

            val selectLanguage = findPreference("changeLanguage") as ListPreference?
            selectLanguage!!.setOnPreferenceChangeListener { _, newValue ->
//                if (newValue.toString() != getLanguage(requireContext())) {
//                    requireActivity().recreate()
//                }
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
    }
}