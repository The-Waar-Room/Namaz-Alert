package com.sudoajay.namaz_alert.ui.home.repository


import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.namaz_alert.R
import com.sudoajay.namaz_alert.data.db.DailyPrayerDB
import com.sudoajay.namaz_alert.databinding.HolderDailyPrayerItemBinding
import com.sudoajay.namaz_alert.ui.BaseFragment.Companion.amText
import com.sudoajay.namaz_alert.ui.BaseFragment.Companion.asrName
import com.sudoajay.namaz_alert.ui.BaseFragment.Companion.dhuhrName
import com.sudoajay.namaz_alert.ui.BaseFragment.Companion.fajrName
import com.sudoajay.namaz_alert.ui.BaseFragment.Companion.ishaName
import com.sudoajay.namaz_alert.ui.BaseFragment.Companion.maghribName
import com.sudoajay.namaz_alert.ui.BaseFragment.Companion.pmText


class DailyPrayerViewHolder(
    val context: Context,
    private val binding: HolderDailyPrayerItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(dailyPrayerDB: DailyPrayerDB, onClick: (String) -> Unit) {

        binding.root.setOnClickListener {
            onClick.invoke(dailyPrayerDB.Name)

        }
        binding.containerConstraintLayout.background = ContextCompat.getDrawable(context,getColor(dailyPrayerDB.Name))
        binding.view.background =ContextCompat.getDrawable(context,getDrawableView(dailyPrayerDB.Name))
        binding.itemBgImage.setImageResource(getDrawableImage(dailyPrayerDB.Name))
        when (dailyPrayerDB.Name) {
            fajrName -> binding.itemTextView.text = context.getString(
                    R.string.fajr_time_text,
                    dailyPrayerDB.Time,
                    getAMOrPM(dailyPrayerDB.Time)
                )
            dhuhrName -> binding.itemTextView.text = context.getString(
                    R.string.dhuhr_time_text,
                    dailyPrayerDB.Time,
                    getAMOrPM(dailyPrayerDB.Time)
                )
            asrName -> binding.itemTextView.text = context.getString(
                    R.string.asr_time_text,
                    dailyPrayerDB.Time,
                    getAMOrPM(dailyPrayerDB.Time)
                )
            maghribName -> binding.itemTextView.text = context.getString(
                    R.string.maghrib_time_text,
                    dailyPrayerDB.Time,
                    getAMOrPM(dailyPrayerDB.Time)
                )
            ishaName -> binding.itemTextView.text = context.getString(
                    R.string.isha_time_text,
                    dailyPrayerDB.Time,
                    getAMOrPM(dailyPrayerDB.Time)
                )
        }
    }

    private fun getColor(prayerName:String): Int {
        return when (prayerName) {
            fajrName -> R.color.fajr_color
            dhuhrName -> R.color.dhuhr_color
            asrName -> R.color.asr_color
            maghribName -> R.color.maghrib_color
            else -> R.color.isha_color
        }
    }

    private fun getDrawableView(prayerName:String): Int {
        return when (prayerName) {
            fajrName -> R.drawable.fajr_home_gradient_drawable
            dhuhrName -> R.drawable.dhuhr_home_gradient_drawable
            asrName -> R.drawable.asr_home_gradient_drawable
            maghribName -> R.drawable.maghrib_home_gradient_drawable
            else -> R.drawable.isha_home_gradient_drawable
        }
    }

    private fun getDrawableImage(prayerName:String): Int {
        return when (prayerName) {
            fajrName -> R.drawable.fajr_home_image
            dhuhrName -> R.drawable.dhuhr_home_image
            asrName -> R.drawable.asr_home_image
            maghribName -> R.drawable.maghrib_home_image
            else -> R.drawable.isha_home_image
        }
    }

    private fun getAMOrPM(time: String): String {
        val hour = time.split(":")[0]
        return if (hour.toInt() < 12) amText else pmText
    }


    private fun getSize(width: Int, heigth: Int): String {
        return "($width * $heigth)"
    }


}