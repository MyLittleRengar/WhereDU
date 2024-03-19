package com.project.wheredu
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import com.google.gson.Gson
object Service {
    private lateinit var retrofit: Retrofit
    private const val url = BuildConfig.SERVER_IP

    fun getService(): ApiService {
        if (!::retrofit.isInitialized) {
            initializeRetrofit()
        }
        return retrofit.create(ApiService::class.java)
    }

    private fun initializeRetrofit() {
        val gson: Gson = GsonBuilder().setLenient().create()
        retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}