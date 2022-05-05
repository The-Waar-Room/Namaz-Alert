package com.sudoajay.namaz_alert.model.pojo

import com.google.gson.annotations.SerializedName
import com.sudoajay.namaz_alert.model.pojo.Today
import com.sudoajay.namaz_alert.model.pojo.Tomorrow
import java.io.Serializable


data class DailyPrayer(
    @SerializedName("city")val city: String,
    @SerializedName("date")val date: String,
    @SerializedName("today")val today: Today,
    @SerializedName("tomorrow")val tomorrow: Tomorrow
): Serializable