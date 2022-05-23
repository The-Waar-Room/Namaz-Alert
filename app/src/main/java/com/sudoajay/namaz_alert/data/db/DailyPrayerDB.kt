package com.sudoajay.namaz_alert.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "DailyPrayerTable")
class DailyPrayerDB(
    @PrimaryKey @field:SerializedName("id") var id: Long?,
    @field:SerializedName("date") val Date: String,
    @field:SerializedName("name") val Name: String,
    @field:SerializedName("time") val Time: String
)