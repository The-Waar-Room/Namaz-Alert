package com.sudoajay.namaz_alert.ui.home.repository

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sudoajay.namaz_alert.data.db.DailyPrayerDB
import com.sudoajay.namaz_alert.databinding.HolderDailyPrayerItemBinding
import javax.inject.Inject

class DailyPrayerAdapter @Inject constructor(
    private val onClick: (DailyPrayerDB) -> Unit
) : PagingDataAdapter<DailyPrayerDB, DailyPrayerViewHolder>(Person_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DailyPrayerViewHolder(
            parent.context,
            HolderDailyPrayerItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: DailyPrayerViewHolder, position: Int) {

        getItem(position)?.let { holder.bind(it, onClick) }


    }


    companion object {
        private val Person_COMPARATOR = object : DiffUtil.ItemCallback<DailyPrayerDB>() {
            override fun areItemsTheSame(oldItem: DailyPrayerDB, newItem: DailyPrayerDB): Boolean =
                oldItem.id == newItem.id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: DailyPrayerDB,
                newItem: DailyPrayerDB
            ): Boolean =
                oldItem == newItem
        }
    }


}