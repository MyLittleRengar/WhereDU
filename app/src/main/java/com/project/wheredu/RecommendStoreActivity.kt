package com.project.wheredu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.wheredu.api.KakaoAPI
import com.project.wheredu.recycler.MainPlaceAdapter
import com.project.wheredu.recycler.MainPlaceItem
import com.project.wheredu.recycler.ResultSearchKeyword
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecommendStoreActivity : AppCompatActivity() {

    companion object {
        const val BASE_URL = BuildConfig.KAKAOURL
        const val API_KEY = BuildConfig.KAKAOAPI
    }
    private var listItems = arrayListOf<MainPlaceItem>()
    private var listAdapter = MainPlaceAdapter(listItems)

    private val cafe = "CE7"

    private lateinit var lm: LocationManager
    private var userNewLocation: Location? = null

    private lateinit var recommendCafeRv: RecyclerView
    private lateinit var recommendCafeBackIv: ImageView

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend_store)

        recommendCafeRv = findViewById(R.id.recommendCafeRV)
        recommendCafeBackIv = findViewById(R.id.recommendCafeBackIV)

        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        userNewLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        recommendCafeRv.layoutManager = LinearLayoutManager(this@RecommendStoreActivity, LinearLayoutManager.VERTICAL, false)
        recommendCafeRv.adapter = listAdapter

        searchCategory()

        listAdapter.setItemClickListener(object: MainPlaceAdapter.OnItemClickListener {
            override fun onClick(url: String) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                val chooser = Intent.createChooser(intent, "웹 브라우저를 선택하세요")
                if(chooser.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
                else {
                    ToastMessage.show(this@RecommendStoreActivity, "웹 브라우저를 실행할 수 없습니다")
                }
            }
        })

        recommendCafeBackIv.setOnClickListener { finish() }
    }

    private fun searchCategory() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)
        val call = api.getSearchCategory(API_KEY, cafe,userNewLocation?.latitude!!.toString(), userNewLocation?.longitude!!.toString(), 2000, 2, 15)

        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
                addItems(response.body())
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
}