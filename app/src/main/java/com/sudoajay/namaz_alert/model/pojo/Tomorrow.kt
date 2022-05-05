package com.sudoajay.namaz_alert.model.pojo

import com.google.gson.annotations.SerializedName

data class Tomorrow(
    @SerializedName("Fajr")val Fajr: String,
    @SerializedName("Sunrise")val Sunrise: String,
    @SerializedName("Dhuhr")val Dhuhr: String,
    @SerializedName("Asr")val Asr: String,
    @SerializedName("Maghrib")val Maghrib: String,
    @SerializedName("Isha'a")val Ishaa: String
)