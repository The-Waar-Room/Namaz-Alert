package com.sudoajay.namaz_alert.data.network


import com.sudoajay.namaz_alert.data.pojo.DailyPrayer
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface DailyPrayerApiInterface {


    @GET("/api/{location}")
    suspend fun getAllCharacters(
        @Path(
            value = "location",
            encoded = true
        ) location: String?
    ): DailyPrayer


    @GET("/api/{location}")
     fun getAllCharacter(
        @Path(
            value = "location",
            encoded = true
        ) location: String?
    ): Call<DailyPrayer>?
    companion object {
        const val Base_URL = "https://dailyprayer.abdulrcs.repl.co"
        const val NETWORK_PAGE_SIZE = 1
    }


}