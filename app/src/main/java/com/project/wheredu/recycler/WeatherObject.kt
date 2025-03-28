package com.project.wheredu.recycler

import com.project.wheredu.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherObject {
    private fun getRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BuildConfig.URL_WEATHER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun getRetrofitService(): WeatherInterface {
        return  getRetrofit().create(WeatherInterface::class.java)
    }
}