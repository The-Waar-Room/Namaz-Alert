package com.sudoajay.namaz_alert.ui.home.repository


import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.namaz_alert.data.db.DailyPrayerAbdulrcsDB
import com.sudoajay.namaz_alert.databinding.HolderDailyPrayerItemBinding


class PersonViewHolder(
    val context: Context,
    private val binding: HolderDailyPrayerItemBinding
) : RecyclerView.ViewHolder(binding.root) {





    fun bind(dailyPrayerAbdulrcsDB: DailyPrayerAbdulrcsDB) {

        Log.e("SomethingNew", "dailyPrayerAbdulrcsDB"  + dailyPrayerAbdulrcsDB.Asr)
    }



    private fun getSize(width: Int, heigth: Int): String {
        return "($width * $heigth)"
    }


}