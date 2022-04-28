package com.sudoajay.namaz_alert.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "DailyPrayerAbdulrcsDBTable")
class DailyPrayerAbdulrcsDB(
    @PrimaryKey @field:SerializedName("id") var id: Long?,
    @field:SerializedName("city") val City:String,
    @field:SerializedName("date") val Date: String,
    @field:SerializedName("Fajr") val Fajr: String,
    @field:SerializedName("Sunrise") val Sunrise: String,
    @field:SerializedName("Dhuhr") val Dhuhr: String,
    @field:SerializedName("Asr") val Asr: String,
    @field:SerializedName("Maghrib") val Maghrib: String,
    @field:SerializedName("Ishaa") val Ishaa: String
)