package com.sudoajay.namaz_alert.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class DailyPrayerBuilder {
    companion object {
        var dailyPrayerApiInterface: DailyPrayerApiInterface? = null
        private var okHttpClient: OkHttpClient? = null

        fun getApiInterface(): DailyPrayerApiInterface? {
            if (dailyPrayerApiInterface == null) {

                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

                okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .callTimeout(50, TimeUnit.SECONDS)
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(DailyPrayerApiInterface.Base_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient!!)
                    .build()
                dailyPrayerApiInterface = retrofit.create(DailyPrayerApiInterface::class.java)

            }
            return dailyPrayerApiInterface
        }

    }

}