package com.sudoajay.namaz_alert.data.network


import com.sudoajay.namaz_alert.data.pojo.DailyPrayerAbdulrcs
import retrofit2.http.GET
import retrofit2.http.Path


interface DailyPrayerAbdulrcslApiInterface {


    @GET("/api/{location}")
    suspend fun getAllCharacters(
        @Path(
            value = "location",
            encoded = true
        ) location: String?
    ): DailyPrayerAbdulrcs


    companion object {
        const val Base_URL = "https://dailyprayer.abdulrcs.repl.co"
        const val NETWORK_PAGE_SIZE = 1
    }


}