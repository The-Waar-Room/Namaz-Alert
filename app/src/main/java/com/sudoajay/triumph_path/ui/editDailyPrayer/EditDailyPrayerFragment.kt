package com.sudoajay.triumph_path.ui.editDailyPrayer

import android.R.string
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.NumberPicker
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sudoajay.triumph_path.R
import com.sudoajay.triumph_path.databinding.FragmentEditDailyPrayerBinding
import com.sudoajay.triumph_path.ui.BaseActivity.Companion.isSystemDefaultOn
import com.sudoajay.triumph_path.ui.BaseFragment
import com.sudoajay.triumph_path.util.Helper.Companion.convertTo12Hr
import com.sudoajay.triumph_path.util.Helper.Companion.getMeIncrementTime
import com.sudoajay.triumph_path.util.Helper.Companion.getPrayerGapTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EditDailyPrayerFragment : BaseFragment() {
    private var isDarkTheme: Boolean = false

    private var _binding: FragmentEditDailyPrayerBinding? = null
    private val binding get() = _binding!!

    private var prayerGapTime: String = ""
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
        fetchDataFromProto()

        requireActivity().changeStatusBarColor(
            ContextCompat.getColor(
                requireContext(),
                getColor()
            ), false
        )

        _binding = FragmentEditDailyPrayerBinding.inflate(inflater, container, false)
        binding.fragment = this
        binding.lifecycleOwner = this
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

        binding.materialCardViewLeftHandSide.setOnClickListener {
            leftHandSidePickerCustom()

        }

        binding.materialCardViewRightHandSide.setOnClickListener {
            rightHandSidePickerCustom()
        }

    }

    private fun leftHandSidePickerCustom() {

        val exactTime = binding.leftHandSideTextView.text.replace("(\\s.+)".toRegex(), "")
        val arrayTime = exactTime.split(":")



        val exactHour = arrayTime[0].toInt()
        val exactMinute = arrayTime[1].toInt()

        val currentTime =
            convertTo12Hr(requireContext(), prayerTime).replace("(\\s.+)".toRegex(), "").split(":")
        val currentHour = currentTime[0].toInt()
        val currentMinute = currentTime[1].toInt()


        val d = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.layout_hour_minute_picker_dialog, null)
        d.setView(dialogView)

        val hourPicker = dialogView.findViewById<NumberPicker>(R.id.dialog_hour_picker)
        val minutePicker = dialogView.findViewById<NumberPicker>(R.id.dialog_minute_picker)
        val numbers = mutableListOf<Int>()

        if(currentHour+2 > 12) {

            for (i in (currentHour-2)..(currentHour+2)) {

                numbers.add(if(i > 12) i % 12 else i)
                Log.e(
                    "NewTag",
                    "numbers  - $numbers  i $i "
                )
            }
            hourPicker.displayedValues = numbers.map { it.toString()  }.toTypedArray()
            //set the min value
            hourPicker.minValue=1
            hourPicker.maxValue=numbers.size
            hourPicker.value = numbers.indexOf(exactHour)+1

            Log.e(
                "NewTag",
                "numbers.indexOf(exactHour)+1  - ${numbers.indexOf(exactHour)+1}  numbers.size $numbers.size "
            )
        }else if(currentHour-2 <= 0){
            for (i in (currentHour-2)..(currentHour+2)) {

                numbers.add(if(i <= 0) 12+i else i)
                Log.e(
                    "NewTag",
                    "numbers  - $numbers  i $i "
                )
            }
            hourPicker.displayedValues = numbers.map { it.toString()  }.toTypedArray()
            //set the min value
            hourPicker.minValue=1
            hourPicker.maxValue=numbers.size
            hourPicker.value = numbers.indexOf(exactHour)+1

            Log.e(
                "NewTag",
                "numbers.indexOf(exactHour)+1  - ${numbers.indexOf(exactHour)+1}  numbers.size $numbers.size "
            )

        }
        else{
            hourPicker.maxValue = currentHour +2
            hourPicker.minValue = currentHour - 2
            hourPicker.value = exactHour
        }


//        hourPicker.value = exactHour + if ( currentHour+2 >= 12 && exactHour <= 2   ) -1 else 0


        hourPicker.wrapSelectorWheel = false

        Log.e(
            "NewTag",
            "exactHour  - $exactHour  currentHour $currentHour "
        )
        minutePicker.maxValue = 60
        minutePicker.minValue = 1
        minutePicker.value = exactMinute
        minutePicker.wrapSelectorWheel = true


        d.setPositiveButton(getString(R.string.set_text)) { _, _ ->


            val selectedHour = if(currentHour+2 > 12)
                if(currentHour+numbers[hourPicker.value-1]  - currentHour <= 4) currentHour+numbers[hourPicker.value-1]
                else  numbers[hourPicker.value-1]
            else if(currentHour-2 <= 0)
                if(numbers[hourPicker.value-1]-12 >= -4 ) numbers[hourPicker.value-1]-12
                else  numbers[hourPicker.value-1]
            else hourPicker.value



            val selectedMinute = minutePicker.value

            val gapTime =
                if (currentHour == selectedHour && currentMinute - selectedMinute >= 0) currentMinute - selectedMinute else (currentHour-selectedHour)*60 + (currentMinute - selectedMinute)
            Log.e(
                "NewTag",
                "gapTime  - $gapTime  currentMinute $currentMinute selectedMinute $selectedMinute  currentHour $currentHour selectedHour $selectedHour"
            )

            if (((currentHour+2) == selectedHour && currentMinute < selectedMinute)) {
                throwToaster(getString(R.string.you_cant_select_30min_text))

            } else if (gapTime > 120) {
                throwToaster(getString(R.string.you_cant_select_30min_text))
            } else {
                val time = "$selectedHour:$selectedMinute"
                setLeftHand(time)


                val afterTime = prayerGapTime.split(":")[1].toInt()
                Log.e(
                    "NewTag",
                    "gapTime  - $afterTime  - gap time , $gapTime $prayerGapTime prayerGapTime"
                )
                setGapInProto(-gapTime, afterTime)



                throwToaster(getString(R.string.successfully_selected_text))
            }
        }
        d.setNegativeButton(getString(R.string.cancel_text)) { _, _ -> }
        val alertDialog = d.create()
        alertDialog.show()
    }

    private fun rightHandSidePickerCustom() {

        val beforeTime = prayerGapTime.split(":")[0].toInt()

        Log.e("EditShow" , " " + binding.rightHandSideTextView.text.toString())


        val getAfterIncrement =
            binding.rightHandSideTextView.text.toString().split(":")[0].replace("(\\s.+)".toRegex(), "").toInt()
        Log.e("EditShow" , " getAfterIncrement $getAfterIncrement")
        val d = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.layout_minute_picker_dialog, null)
        d.setView(dialogView)
        val minutePicker = dialogView.findViewById<NumberPicker>(R.id.dialog_minute_picker)

        minutePicker.maxValue = 30
        minutePicker.minValue = 0
        minutePicker.value = getAfterIncrement
        minutePicker.wrapSelectorWheel = false

        d.setPositiveButton(getString(R.string.set_text)) { _, i ->

            Log.e(
                "EditShow",
                "  minutePicker.value  + beforeTime  getAfterIncrement +  "  + " $getAfterIncrement  --  " + + (minutePicker.value + beforeTime) + "  minutePicker ${minutePicker.value}  beforeTime $beforeTime "
            )
            if (minutePicker.value  > 30) {
                throwToaster(
                    getString(
                        R.string.you_cant_past_select_30min_text
                    )
                )
            } else {
                setRightHand(minute = minutePicker.value)
                setGapInProto(beforeTime, minutePicker.value )
                throwToaster(getString(R.string.successfully_selected_text))
            }
        }
        d.setNegativeButton(getString(R.string.cancel_text)) { dialogInterface, i -> }
        val alertDialog = d.create()
        alertDialog.show()
    }


    private fun setText() {
        setLeftHand()
        when (prayerName) {
            fajrName -> {
                binding.dailyPrayerTextView.text = getString(
                    R.string.fajr_namaz_time_text,
                    convertTo12Hr(requireContext(), prayerTime)
                )
                setImageViewHeight(230f)
            }

            dhuhrName -> {
                binding.dailyPrayerTextView.text = getString(
                    R.string.dhuhr_namaz_time_text,
                    convertTo12Hr(requireContext(), prayerTime)
                )
                setImageViewHeight(300f)
            }

            asrName -> binding.dailyPrayerTextView.text = getString(
                R.string.asr_namaz_time_text,
                convertTo12Hr(requireContext(), prayerTime)
            )
            maghribName -> binding.dailyPrayerTextView.text = getString(
                R.string.maghrib_namaz_time_text,
                convertTo12Hr(requireContext(), prayerTime)
            )
            else -> {
                binding.dailyPrayerTextView.text = getString(
                    R.string.isha_namaz_time_text,
                    convertTo12Hr(requireContext(), prayerTime)
                )
                setImageViewHeight(280f)
            }
        }
    }

    private fun fetchDataFromProto() {
        lifecycleScope.launch {
            val waitFor = CoroutineScope(Dispatchers.IO).async {
                prayerGapTime = getPrayerGapTime(prayerName, protoManager)
                Log.e("NewTag", "Prayer Time $prayerName $protoManager $prayerGapTime ")

                return@async prayerGapTime
            }
            waitFor.await()
        }
    }

    private fun resetGapTimingPrayer() {
        prayerGapTime = getString(R.string.default_prayer_time_proto)
        setLeftHand()
        setGapInProto(-5, 10)
        fetchDataFromProto()
        throwToaster(getString(R.string.time_reset_successfully_text))
    }

    fun askAfterResetClick() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.reset_text))
            .setMessage(getString(R.string.do_you_want_to_set_text))
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(
                R.string.yes_string
            ) { _, _ ->
                resetGapTimingPrayer()
            }
            .setNegativeButton(R.string.no_string, null).show()
    }

    private fun setGapInProto(beforeGap: Int, afterGap: Int) {
        lifecycleScope.launch {
            when (prayerName) {
                fajrName -> protoManager.setFajrTiming("$beforeGap:$afterGap")
                dhuhrName -> protoManager.setDhuhrTiming("$beforeGap:$afterGap")
                asrName -> protoManager.setAsrTiming("$beforeGap:$afterGap")
                maghribName -> protoManager.setMaghribTiming("$beforeGap:$afterGap")
                else -> protoManager.setIshaTiming("$beforeGap:$afterGap")
            }
        }
    }

    private fun setImageViewHeight(value: Float) {
        val layout: ImageView = binding.itemBgImage
        val params: ViewGroup.LayoutParams = layout.layoutParams
        val dpHeight =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)
        params.height = dpHeight.toInt()
        layout.layoutParams = params
    }

    private fun setLeftHand(time: String = "") {
        var newTime = time
        val newMinute: Int
        if (time == "") {
            val gapBeforePrayer = prayerGapTime.split(":")[0].toInt()

            newMinute = prayerGapTime.split(":")[1].toInt()
            Log.e(
                "NewTag",
                "   gapBeforePrayer +   $gapBeforePrayer  newMinute $newMinute   prayerGapTime  $prayerGapTime --  ")
            setRightHand(newMinute )
            newTime = getMeIncrementTime(
                prayerTime.replace("(\\s.+)".toRegex(), ""),
                gapBeforePrayer
            )
        }




        binding.leftHandSideTextView.text =
            getString(
                R.string.left_hand_side_time_text,
                convertTo12Hr(requireContext(), newTime).lowercase()
            )
    }

    private fun setRightHand(minute: Int? = null) {

        binding.rightHandSideTextView.text = getString(
            R.string.right_hand_side_time_text,
            minute.toString()
        )

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
