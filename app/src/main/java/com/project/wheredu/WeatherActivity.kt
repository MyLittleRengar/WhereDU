package com.project.wheredu

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.wheredu.recycler.WeatherAdapter
import com.project.wheredu.recycler.WeatherObject
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.project.wheredu.recycler.ITEM
import com.project.wheredu.recycler.ModelWeather
import com.project.wheredu.recycler.WEATHER
import com.project.wheredu.utility.Common
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    private lateinit var tvDate: TextView
    private lateinit var btnRefresh: Button
    private lateinit var weatherRecyclerView: RecyclerView
    private lateinit var tvError: TextView

    private var baseDate = "20240501"
    private var baseTime = "1400"
    private var curPoint : Point? = null
    @SuppressLint("SetTextI18n", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        tvDate = findViewById(R.id.tvDate)
        btnRefresh = findViewById(R.id.btnRefresh)
        weatherRecyclerView = findViewById(R.id.weatherRecyclerView)
        tvError = findViewById(R.id.tvError)

        tvDate.text = SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(Calendar.getInstance().time) + "날씨"

        val permissionList = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

        ActivityCompat.requestPermissions(this, permissionList, 1)

        requestLocation()
        btnRefresh.setOnClickListener {
            requestLocation()
        }
    }

    private fun setWeather(nx : Int, ny : Int) {
        val cal = Calendar.getInstance()
        baseDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
        val timeH = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time)
        val timeM = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time)

        baseTime = Common().getBaseTime(timeH, timeM)
        if (timeH == "00" && baseTime == "2330") {
            cal.add(Calendar.DATE, -1).toString()
            baseDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
        }
        val call = WeatherObject.getRetrofitService().getWeather(60, 1, "JSON", baseDate, baseTime, nx, ny)
        call.enqueue(object : retrofit2.Callback<WEATHER> {
            override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                if (response.isSuccessful) {
                    val it: List<ITEM> = response.body()!!.response.body.items.item
                    val weatherArr = arrayOf(ModelWeather(), ModelWeather(), ModelWeather(), ModelWeather(), ModelWeather(), ModelWeather())

                    var index = 0
                    val totalCount = response.body()!!.response.body.totalCount - 1
                    for (i in 0..totalCount) {
                        index %= 6
                        when(it[i].category) {
                            "PTY" -> weatherArr[index].rainType = it[i].fcstValue
                            "REH" -> weatherArr[index].humidity = it[i].fcstValue
                            "SKY" -> weatherArr[index].sky = it[i].fcstValue
                            "T1H" -> weatherArr[index].temp = it[i].fcstValue
                            else -> continue
                        }
                        index++
                    }
                    weatherArr[0].fcstTime = "지금"
                    for (i in 1..5) weatherArr[i].fcstTime = it[i].fcstTime
                    weatherRecyclerView.adapter = WeatherAdapter(weatherArr)
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<WEATHER>, t: Throwable) {
                tvError.text = "api fail : " +  t.message.toString() + "\n 다시 시도해주세요."
                tvError.visibility = View.VISIBLE
                Log.d("api fail", t.message.toString())
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val locationClient = LocationServices.getFusedLocationProviderClient(this@WeatherActivity)
        try {
            val locationRequest = LocationRequest.create()
            locationRequest.run {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 60 * 1000
            }
            val locationCallback = object : LocationCallback() {
                @SuppressLint("SetTextI18n")
                override fun onLocationResult(p0: LocationResult) {
                    p0.let {
                        for (location in it.locations) {
                            curPoint = Common().dfsXyConv(location.latitude, location.longitude)
                            tvDate.text = SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(Calendar.getInstance().time) + " 날씨"
                            setWeather(curPoint!!.x, curPoint!!.y)
                        }
                    }
                }
            }
            Looper.myLooper()?.let {
                locationClient.requestLocationUpdates(locationRequest, locationCallback,
                    it)
            }
        } catch (e : SecurityException) {
            e.printStackTrace()
        }
    }
}