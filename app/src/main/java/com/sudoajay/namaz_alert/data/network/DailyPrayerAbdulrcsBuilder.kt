package com.sudoajay.namaz_alert.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class DailyPrayerAbdulrcsBuilder {
    companion object {
        var marvelApiInterface: DailyPrayerAbdulrcslApiInterface? = null
        private var okHttpClient: OkHttpClient? = null

        fun getApiInterface(): DailyPrayerAbdulrcslApiInterface? {
            if (marvelApiInterface == null) {

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
                    .baseUrl(DailyPrayerAbdulrcslApiInterface.Base_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient!!)
                    .build()
                marvelApiInterface = retrofit.create(DailyPrayerAbdulrcslApiInterface::class.java)

            }
            return marvelApiInterface
        }

    }

}