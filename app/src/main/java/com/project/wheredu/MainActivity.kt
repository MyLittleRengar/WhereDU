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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.wheredu.api.KakaoAPI
import com.project.wheredu.api.Service
import com.project.wheredu.friend.FriendsActivity
import com.project.wheredu.promise.PromiseActivity
import com.project.wheredu.promise.PromiseAdd1Activity
import com.project.wheredu.recycler.MainPlaceAdapter
import com.project.wheredu.recycler.MainPlaceItem
import com.project.wheredu.recycler.ResultSearchKeyword
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        const val BASE_URL = BuildConfig.KAKAOURL
        const val API_KEY = BuildConfig.KAKAOAPI
    }

    private lateinit var promiseAddIv: ImageView
    private lateinit var nearStoreRv: RecyclerView
    private lateinit var recommendCafeRv: RecyclerView
    private lateinit var nearStoreInfoTv: TextView
    private lateinit var recommendCafeInfoTv: TextView

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

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        promiseAddIv = findViewById(R.id.promiseAddIv)
        nearStoreRv = findViewById(R.id.nearStoreRv)
        recommendCafeRv = findViewById(R.id.recommendCafeRv)
        nearStoreInfoTv = findViewById(R.id.nearStoreInfoTV)
        recommendCafeInfoTv = findViewById(R.id.recommendCafeInfoTV)

        //val distance = Distance.calculateAndFormatDistance(35.9124703, 128.8188155, 35.9026591, 128.8563364)
        //Log.d("Distance", distance)

        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        userNewLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        searchCategory()
        searchCategory2()

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

        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        val storeID = preferences.getString("accountID", "").toString()
        val storeNick = preferences.getString("accountNickname", "").toString()
        if(storeNick == "") {
            returnNickname(storeID)
        }

        promiseAddIv.setOnClickListener {
            startActivity(Intent(this@MainActivity, PromiseAdd1Activity::class.java))
            finish()
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
                val item = MainPlaceItem(
                    R.drawable.cafe,
                    document.place_name,
                    document.phone.ifEmpty { "없음" },
                    PlaceDistance.calculateAndFormatDistance(userNewLocation?.latitude!!,userNewLocation?.longitude!!, document.x.toDouble(), document.y.toDouble())+"m",
                    document.place_url,
                    document.x.toDouble(),
                    document.y.toDouble())
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
                val item = MainPlaceItem(
                    R.drawable.store,
                    document.place_name,
                    document.phone.ifEmpty { "없음" },
                    PlaceDistance.calculateAndFormatDistance(userNewLocation?.latitude!!,userNewLocation?.longitude!!, document.x.toDouble(), document.y.toDouble())+"m",
                    document.place_url,
                    document.x.toDouble(),
                    document.y.toDouble())
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
}