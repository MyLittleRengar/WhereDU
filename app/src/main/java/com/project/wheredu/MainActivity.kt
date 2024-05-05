package com.project.wheredu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.wheredu.utility.KakaoAPI
import com.project.wheredu.utility.Service
import com.project.wheredu.friend.FriendsActivity
import com.project.wheredu.promise.PastListActivity
import com.project.wheredu.promise.PromiseActivity
import com.project.wheredu.promise.PromiseAdd1Activity
import com.project.wheredu.recycler.MainPlaceAdapter
import com.project.wheredu.recycler.MainPlaceItem
import com.project.wheredu.recycler.ResultSearchKeyword
import com.project.wheredu.utility.PlaceDistance
import com.project.wheredu.utility.ToastMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity() {

    companion object {
        const val BASE_URL = BuildConfig.KAKAOURL
        const val API_KEY = BuildConfig.KAKAOAPI
    }

    private lateinit var promiseTitleTv: TextView
    private lateinit var promiseRemainTimeTv: TextView
    private lateinit var promiseLocationTv: TextView
    private lateinit var pastListIv: ImageView
    private lateinit var promiseAddIv: ImageView
    private lateinit var mainPromiseEnterBtn: Button
    private lateinit var nearStoreRv: RecyclerView
    private lateinit var recommendCafeRv: RecyclerView
    private lateinit var nearStoreInfoTv: TextView
    private lateinit var recommendCafeInfoTv: TextView

    private lateinit var mainDutchPayBtn: Button
    private lateinit var mainWeatherBtn: Button
    private lateinit var mainRandomPickBtn: Button

    private lateinit var mainBottomNav: BottomNavigationView

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val service = Service.getService()

    private var listItems = arrayListOf<MainPlaceItem>()
    private var listAdapter = MainPlaceAdapter(listItems)

    private var listItems2 = arrayListOf<MainPlaceItem>()
    private var listAdapter2 = MainPlaceAdapter(listItems2)

    private val cafe = "CE7"
    private val restaurant = "FD6"

    private lateinit var lm: LocationManager
    private var userNewLocation: Location? = null

    private var promiseLatitude: Double? = null
    private var promiseLongitude: Double? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        val storeID = preferences.getString("accountID", "").toString()
        val storeNick = preferences.getString("accountNickname", "").toString()
        if(storeNick == "") {
            returnNickname(storeID)
        }

        promiseTitleTv = findViewById(R.id.promiseTitleTv)
        promiseRemainTimeTv = findViewById(R.id.promiseRemainTimeTV)
        promiseLocationTv = findViewById(R.id.promiseLocationTV)
        pastListIv = findViewById(R.id.pastListIv)
        promiseAddIv = findViewById(R.id.promiseAddIv)
        mainPromiseEnterBtn = findViewById(R.id.mainPromiseEnterBTN)
        nearStoreRv = findViewById(R.id.nearStoreRv)
        recommendCafeRv = findViewById(R.id.recommendCafeRv)
        nearStoreInfoTv = findViewById(R.id.nearStoreInfoTV)
        recommendCafeInfoTv = findViewById(R.id.recommendCafeInfoTV)

        mainDutchPayBtn = findViewById(R.id.mainDutchPayBtn)
        mainWeatherBtn = findViewById(R.id.mainWeatherBtn)
        mainRandomPickBtn = findViewById(R.id.mainRandomPickBtn)

        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        userNewLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        searchCategory()
        searchCategory2()
        recentPromise(storeNick)

        mainPromiseEnterBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, MapActivity::class.java)
            intent.putExtra("promiseName", promiseTitleTv.text.toString())
            intent.putExtra("promiseLatitude", promiseLatitude)
            intent.putExtra("promiseLongitude", promiseLongitude)
            startActivity(intent)
        }

        nearStoreInfoTv.setOnClickListener {
            startActivity(Intent(this@MainActivity, NearStoreActivity::class.java))
        }
        recommendCafeInfoTv.setOnClickListener {
            startActivity(Intent(this@MainActivity, RecommendStoreActivity::class.java))
        }

        recommendCafeRv.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        recommendCafeRv.adapter = listAdapter

        listAdapter.setItemClickListener(object: MainPlaceAdapter.OnItemClickListener {
            override fun onClick(url: String) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                val chooser = Intent.createChooser(intent, "웹 브라우저를 선택하세요")
                if(chooser.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
                else {
                    ToastMessage.show(this@MainActivity, "웹 브라우저를 실행할 수 없습니다")
                }
            }
        })

        nearStoreRv.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        nearStoreRv.adapter = listAdapter2
        listAdapter2.setItemClickListener(object: MainPlaceAdapter.OnItemClickListener {
            override fun onClick(url: String) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                val chooser = Intent.createChooser(intent, "웹 브라우저를 선택하세요")
                if(chooser.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
                else {
                    ToastMessage.show(this@MainActivity, "웹 브라우저를 실행할 수 없습니다")
                }
            }
        })

        mainBottomNav = findViewById(R.id.main_bottomNav)

        pastListIv.setOnClickListener {
            startActivity(Intent(this@MainActivity, PastListActivity::class.java))
        }

        promiseAddIv.setOnClickListener {
            startActivity(Intent(this@MainActivity, PromiseAdd1Activity::class.java))
            finish()
        }

        mainDutchPayBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, DutchPayActivity::class.java))
        }
        mainWeatherBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, WeatherActivity::class.java))
        }
        mainRandomPickBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, RandomPickActivity::class.java))
        }

        mainBottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.main_promise -> {
                    startActivity(Intent(this@MainActivity, PromiseActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }
                R.id.main_friend -> {
                    startActivity(Intent(this@MainActivity, FriendsActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }
                R.id.main_mypage -> {
                    startActivity(Intent(this@MainActivity, MyPageActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }

                else -> {
                    return@setOnItemSelectedListener true
                }
            }
        }
    }

    private fun searchCategory() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)
        val call = api.getSearchCategory(API_KEY, cafe,userNewLocation?.latitude!!.toString(), userNewLocation?.longitude!!.toString(), 1000, 1, 15)

        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
                addItems(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                Log.w("MainActivity", "통신 실패: ${t.message}")
            }
        })
    }

    private fun searchCategory2() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)
        val call = api.getSearchCategory(API_KEY, restaurant,userNewLocation?.latitude!!.toString(), userNewLocation?.longitude!!.toString(), 1000, 1, 15)

        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
                addItems2(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                Log.w("MainActivity", "통신 실패: ${t.message}")
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addItems(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            listItems.clear()
            for (document in searchResult!!.documents) {
                val distance = PlaceDistance.calculateAndFormatDistance(userNewLocation?.latitude!!.toDouble(),userNewLocation?.longitude!!.toDouble(), document.y.toDouble(), document.x.toDouble())
                val item = MainPlaceItem(
                    R.drawable.cafe,
                    document.place_name,
                    document.phone.ifEmpty { "없음" },
                    distance,
                    document.place_url,
                    document.x.toDouble(),
                    document.y.toDouble())
                listItems.sortBy { it.distance }
                listItems.add(item)
            }
            listAdapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addItems2(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            listItems2.clear()
            for (document in searchResult!!.documents) {
                val distance = PlaceDistance.calculateAndFormatDistance(userNewLocation?.latitude!!.toDouble(),userNewLocation?.longitude!!.toDouble(), document.y.toDouble(), document.x.toDouble())
                val item = MainPlaceItem(
                    R.drawable.store,
                    document.place_name,
                    document.phone.ifEmpty { "없음" },
                    distance,
                    document.place_url,
                    document.x.toDouble(),
                    document.y.toDouble())
                listItems.sortBy { it.distance }
                listItems2.add(item)
            }
            listAdapter2.notifyDataSetChanged()
        }
    }

    private fun returnNickname(userID: String) {
        val callPost = service.getUserData(userID)
        callPost.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        editor = preferences.edit()
                        editor.putString("accountNickname", result)
                        editor.apply()
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@MainActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@MainActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun recentPromise(name: String) {
        val callPost = service.recentPromise(name)
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result != "noData") {
                            promiseRemainTimeTv.visibility = View.VISIBLE
                            promiseLocationTv.visibility = View.VISIBLE
                            mainPromiseEnterBtn.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.DUGreen))
                            mainPromiseEnterBtn.isEnabled = true
                            replaceData(result)
                        }
                        else {
                            promiseTitleTv.text = "약속 없음"
                            promiseRemainTimeTv.visibility = View.GONE
                            promiseLocationTv.visibility = View.GONE
                            mainPromiseEnterBtn.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.gray))
                            mainPromiseEnterBtn.isEnabled = false
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@MainActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@MainActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun replaceData(result: String) {
        val replace = result.replace("[","").replace("{","").replace("\"","").replace("}","").replace("]","")
            .replace("promiseName:","").replace("promisePlace:","").replace("promiseTime:","").replace("promiseDate:","").replace("promiseLatitude:", "").replace("promiseLongitude:", "")
        val textSplit = replace.split(",")

        val formatter = DateTimeFormatter.ofPattern("yyyy.M.dd")
        val given = LocalDate.parse(textSplit[3], formatter)
        val current = LocalDate.now()
        val difference = ChronoUnit.DAYS.between(current, given)
        if(given == current) {
            val currentTime = LocalTime.now()
            val targetTimeString = textSplit[2]
            val targetTime = LocalTime.parse(targetTimeString, DateTimeFormatter.ofPattern("HH:mm"))
            val remainingTime = targetTime.toSecondOfDay() - currentTime.toSecondOfDay()
            val hours = remainingTime / 3600
            val minutes = (remainingTime % 3600) / 60

            val resultTime = "약속까지 ${hours}시간 ${minutes}분 남았어요!"

            promiseTitleTv.text = textSplit[0]
            promiseLocationTv.text = "${textSplit[1]}(으)로 가야해요"
            promiseRemainTimeTv.text = resultTime
            promiseLatitude = textSplit[4].toDouble()
            promiseLongitude = textSplit[5].toDouble()
        }
        else if(difference >= 2) {
            promiseTitleTv.text = "하루 이내 약속 없음"
            promiseRemainTimeTv.visibility = View.GONE
            promiseLocationTv.visibility = View.GONE
            mainPromiseEnterBtn.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.gray))
        }
        else {
            val kstZoneId = ZoneId.of("Asia/Seoul")
            val now = LocalDateTime.now(kstZoneId)
            val endOfDay = LocalDateTime.of(LocalDate.now(kstZoneId), LocalTime.MAX)
            val remainingTime = ChronoUnit.MINUTES.between(now, endOfDay)

            val remainingHours = remainingTime / 60
            val remainingMinutes = remainingTime % 60

            val times = textSplit[2].split(":")
            val calculationHour = remainingHours.toInt() + times[0].toInt()
            val calculationMinute = remainingMinutes.toInt() + times[1].toInt()
            val resultTime = "약속까지 ${calculationHour}시간 ${calculationMinute}분 남았어요!"

            promiseTitleTv.text = textSplit[0]
            promiseLocationTv.text = textSplit[1]+"(으)로 가야해요"
            promiseRemainTimeTv.text = resultTime
            promiseLatitude = textSplit[4].toDouble()
            promiseLongitude = textSplit[5].toDouble()
        }
    }
}