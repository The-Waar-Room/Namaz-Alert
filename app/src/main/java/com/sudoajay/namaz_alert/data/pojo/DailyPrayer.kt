package com.sudoajay.namaz_alert.data.pojo

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable


data class DailyPrayer(
    @SerializedName("city")val city: String,
    @SerializedName("date")val date: String,
    @SerializedName("today")val today: Today,
    @SerializedName("tomorrow")val tomorrow: Tomorrow
): Serializable