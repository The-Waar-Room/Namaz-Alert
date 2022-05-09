package com.sudoajay.namaz_alert.ui.editDailyPrayer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.databinding.FragmentEditDailyPrayerBinding
import com.sudoajay.namaz_alert.ui.BaseActivity.Companion.isSystemDefaultOn
import com.sudoajay.namaz_alert.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class EditDailyPrayerFragment : BaseFragment() {
    private var isDarkTheme: Boolean = false

    private var _binding: FragmentEditDailyPrayerBinding? = null
    private val binding get() = _binding!!

    private var prayerName = fajrName
    private var prayerTime = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        isDarkTheme = isSystemDefaultOn(resources)

        prayerName = arguments?.getString(editDailyPrayerNameKey, fajrName).toString()
        prayerTime = arguments?.getString(editDailyPrayerTimeKey, "").toString()
        lifecycleScope.launch {
            getPhoneModeFromProtoDataStore()

        }
        requireActivity().changeStatusBarColor(
            ContextCompat.getColor(
                requireContext(),
                getColor()
            ), false
        )

        _binding = FragmentEditDailyPrayerBinding.inflate(inflater, container, false)


        setUpView()


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    openHomeFragment()
                }
            })
    }

    private fun setUpView() {
        setText()

        binding.materialToolbar.background = ContextCompat.getDrawable(requireContext(), getColor())
        binding.mainConstraintLayout.background =
            ContextCompat.getDrawable(requireContext(), getColor())
        binding.itemBgImage.setImageResource(getDrawableImage())
        binding.view.background = ContextCompat.getDrawable(requireContext(), getDrawableView())

        binding.materialToolbar.setNavigationOnClickListener {
            // perform whatever you want on back arrow click
            openHomeFragment()
        }

        binding.leftHandSideTextView.setOnClickListener {
            leftHandSidePickerCustom()

        }

        binding.rightHandSideTextView.setOnClickListener {
            rightHandSidePickerCustom()
        }

    }

    private fun leftHandSidePickerCustom() {
        val arrayTime = prayerTime.split(":")
        val currentHour = arrayTime[0].toInt()
        val currentMinute = arrayTime[1].toInt()

        val d = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.layout_hour_minute_picker_dialog, null)
        d.setView(dialogView)

        val hourPicker = dialogView.findViewById<NumberPicker>(R.id.dialog_hour_picker)
        val minutePicker = dialogView.findViewById<NumberPicker>(R.id.dialog_minute_picker)

        hourPicker.maxValue = currentHour
        hourPicker.minValue = currentHour - 1
        hourPicker.value = currentHour
        hourPicker.wrapSelectorWheel = true

        minutePicker.maxValue = 60
        minutePicker.minValue = 1
        minutePicker.value = currentMinute
        minutePicker.wrapSelectorWheel = true


        d.setPositiveButton("Set") { _, i ->

            val selectedHour = hourPicker.value
            val selectedMinute = minutePicker.value

            val lastMinute =
                if (currentMinute - 30 >= 0) currentMinute - 30 else 60 + (currentMinute - 30)
            Log.e("NewTag", "lastminite  - $lastMinute")

            if ((currentHour == selectedHour && currentMinute < selectedMinute)) {
                throwToaster("You can't set time after namaz time")
            } else if ((currentHour == selectedHour && lastMinute > selectedMinute) || (currentHour > selectedHour && lastMinute > selectedMinute)) {
                throwToaster("You can't set time before namaz time 30 min only")
            } else {
                val time = "$selectedHour:$selectedMinute"
                setLeftHand(time)
            }
        }
        d.setNegativeButton("Cancel") { dialogInterface, i -> }
        val alertDialog = d.create()
        alertDialog.show()
    }

    private fun rightHandSidePickerCustom() {

        val d = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.layout_minute_picker_dialog, null)
        d.setView(dialogView)
        val minutePicker = dialogView.findViewById<NumberPicker>(R.id.dialog_minute_picker)

        minutePicker.maxValue = 60
        minutePicker.minValue = 1
        minutePicker.value = 20
        minutePicker.wrapSelectorWheel = false

        d.setPositiveButton("Set") { _, i ->
            setRightHand(minuteIncrement = minutePicker.value)
        }
        d.setNegativeButton("Cancel") { dialogInterface, i -> }
        val alertDialog = d.create()
        alertDialog.show()
    }


    private fun setText() {
        setLeftHand(prayerTime)
        setRightHand(minuteIncrement = 20)
        when (prayerName) {
            fajrName ->
                binding.dailyPrayerTextView.text = getString(
                    R.string.fajr_namaz_time_text,
                    convertTo12Hours(prayerTime)
                )


            dhuhrName -> binding.dailyPrayerTextView.text = getString(
                R.string.dhuhr_namaz_time_text,
                convertTo12Hours(prayerTime)
            )
            asrName -> binding.dailyPrayerTextView.text = getString(
                R.string.asr_namaz_time_text,
                convertTo12Hours(prayerTime)
            )
            maghribName -> binding.dailyPrayerTextView.text = getString(
                R.string.maghrib_namaz_time_text,
                convertTo12Hours(prayerTime)
            )
            else -> binding.dailyPrayerTextView.text = getString(
                R.string.isha_namaz_time_text,
                convertTo12Hours(prayerTime)
            )
        }
    }


    private fun setLeftHand(time: String) {
        binding.leftHandSideTextView.text =
            getString(R.string.left_hand_side_time_text, phoneMode, "$time${getAMOrPM(time)}")
    }

    private fun setRightHand(time: String = prayerTime, minuteIncrement: Int) {
        Log.e("EditView", "Praeyer Timne $prayerTime - PrayerNmae $prayerName")
        val incrementTime = getMeIncrementTime(time, minuteIncrement)
        binding.rightHandSideTextView.text = getString(
            R.string.right_hand_side_time_text,
            phoneMode,
            "$incrementTime${getAMOrPM(incrementTime)}"
        )

    }

    private fun getMeIncrementTime(time: String, minuteIncrement: Int): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        val timeArray = time.split(":")
        val newDate = Calendar.getInstance()
        newDate.set(Calendar.HOUR_OF_DAY, timeArray[0].toInt())
        newDate.set(Calendar.MINUTE, timeArray[1].toInt())
        newDate.add(Calendar.MINUTE, minuteIncrement)
        return sdf.format(newDate.time).toString()
    }


    private fun getColor(): Int {
        return when (prayerName) {
            fajrName -> R.color.fajr_color
            dhuhrName -> R.color.dhuhr_color
            asrName -> R.color.asr_color
            maghribName -> R.color.maghrib_color
            else -> R.color.isha_color
        }
    }

    private fun getDrawableView(): Int {
        return when (prayerName) {
            fajrName -> R.drawable.fajr_bg_gradient_drawable
            dhuhrName -> R.drawable.dhuhr_bg_gradient_drawable
            asrName -> R.drawable.asr_bg_gradient_drawable
            maghribName -> R.drawable.maghrib_bg_gradient_drawable
            else -> R.drawable.isha_bg_gradient_drawable
        }
    }

    private fun getDrawableImage(): Int {
        return when (prayerName) {
            fajrName -> R.drawable.fajr_image
            dhuhrName -> R.drawable.dhuhr_image
            asrName -> R.drawable.asr_image
            maghribName -> R.drawable.maghrib_image
            else -> R.drawable.isha_image
        }
    }


    private fun openHomeFragment() {
        findNavController().navigate(R.id.action_editDailyPrayerFragment_to_homeFragment)
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }


}
