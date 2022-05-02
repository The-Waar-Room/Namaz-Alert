package com.sudoajay.namaz_alert.ui.home.repository


import android.content.Context
import android.util.Log
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

    fun bind(dailyPrayerDB: DailyPrayerDB) {

        when(dailyPrayerDB.Name){
            fajrName-> {
                binding.itemBgImage.setImageResource(R.drawable.fajr_bg_with_image)
                binding.itemTextView.text = context.getString(R.string.fajr_time_text,dailyPrayerDB.Time,getAMOrPM(dailyPrayerDB.Time))
            }
            dhuhrName->{
                binding.itemBgImage.setImageResource(R.drawable.dhuhr_bg_with_image)
                binding.itemTextView.text = context.getString(R.string.dhuhr_time_text,dailyPrayerDB.Time,getAMOrPM(dailyPrayerDB.Time))
            }
            asrName->{
                binding.itemBgImage.setImageResource(R.drawable.asr_bg_with_image)
                binding.itemTextView.text = context.getString(R.string.asr_time_text,dailyPrayerDB.Time,getAMOrPM(dailyPrayerDB.Time))
            }
            maghribName->{
                binding.itemBgImage.setImageResource(R.drawable.maghrib_bg_with_image)
                binding.itemTextView.text = context.getString(R.string.maghrib_time_text,dailyPrayerDB.Time,getAMOrPM(dailyPrayerDB.Time))
            }
            ishaName->{
                binding.itemBgImage.setImageResource(R.drawable.isha_bg_with_image)
                binding.itemTextView.text = context.getString(R.string.isha_time_text,dailyPrayerDB.Time,getAMOrPM(dailyPrayerDB.Time))
            }
        }
    }
    private  fun getAMOrPM(time:String):String{
        val hour = time.split(":")[0]
        return if (hour.toInt() < 12   ) amText else pmText
    }


    private fun getSize(width: Int, heigth: Int): String {
        return "($width * $heigth)"
    }


}