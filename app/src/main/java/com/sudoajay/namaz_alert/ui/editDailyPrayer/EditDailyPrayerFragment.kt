package com.sudoajay.namaz_alert.ui.editDailyPrayer

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.databinding.FragmentEditDailyPrayerBinding
import com.sudoajay.namaz_alert.ui.BaseActivity.Companion.isSystemDefaultOn
import com.sudoajay.namaz_alert.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class EditDailyPrayerFragment : BaseFragment() {
    private var isDarkTheme: Boolean = false

    private var _binding: FragmentEditDailyPrayerBinding? = null
    private val binding get() = _binding!!

    private var prayerName = fajrName


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        isDarkTheme = isSystemDefaultOn(resources)

        prayerName = arguments?.getString(editDailyPrayerNameKey, fajrName).toString()

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
        binding.materialToolbar.background = ContextCompat.getDrawable(requireContext(),getColor())
        binding.mainConstraintLayout.background =ContextCompat.getDrawable(requireContext(),getColor())
        binding.itemBgImage.setImageResource(getDrawableImage())
        binding.view.background =ContextCompat.getDrawable(requireContext(),getDrawableView())

        binding.materialToolbar.setNavigationOnClickListener {
            // perform whatever you want on back arrow click
          openHomeFragment()
        }


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
