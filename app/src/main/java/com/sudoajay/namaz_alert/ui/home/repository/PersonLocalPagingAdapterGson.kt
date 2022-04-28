package com.sudoajay.namaz_alert.ui.home.repository

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sudoajay.namaz_alert.data.db.DailyPrayerAbdulrcsDB
import com.sudoajay.namaz_alert.databinding.HolderDailyPrayerItemBinding
import javax.inject.Inject

class PersonLocalPagingAdapterGson @Inject constructor(
) :
    PagingDataAdapter<DailyPrayerAbdulrcsDB, PersonViewHolder>(Person_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PersonViewHolder(
            parent.context,
            HolderDailyPrayerItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {

        getItem(position)?.let { holder.bind(it) }
    }



    companion object {
        private val Person_COMPARATOR = object : DiffUtil.ItemCallback<DailyPrayerAbdulrcsDB>() {
            override fun areItemsTheSame(oldItem: DailyPrayerAbdulrcsDB, newItem: DailyPrayerAbdulrcsDB): Boolean =
                oldItem.id == newItem.id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: DailyPrayerAbdulrcsDB, newItem: DailyPrayerAbdulrcsDB): Boolean =
                oldItem == newItem
        }
    }


}