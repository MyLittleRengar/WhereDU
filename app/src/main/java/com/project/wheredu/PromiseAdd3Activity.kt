package com.project.wheredu

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.wheredu.recycler.PromisePlaceAdapter
import com.project.wheredu.recycler.PromisePlaceItem
import com.project.wheredu.recycler.ResultSearchKeyword
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PromiseAdd3Activity : AppCompatActivity() {

    companion object {
        const val BASE_URL = BuildConfig.KAKAOURL
        const val API_KEY = BuildConfig.KAKAOAPI
    }

    private lateinit var promiseBack3Iv: ImageView
    private lateinit var promiseDone3Tv: TextView
    private lateinit var promiseSelectPlaceTv: TextView
    private lateinit var promisePlaceTv: TextView
    private lateinit var promisePlaceSearchEt: EditText
    private lateinit var promisePlaceSearchBtn: Button
    private lateinit var promisePlaceSearchRv: RecyclerView

    private var listItems = arrayListOf<PromisePlaceItem>()
    private var listAdapter = PromisePlaceAdapter(listItems)
    private var keyword = ""
    private var promiseLatitude: Double? = null
    private var promiseLongitude: Double? = null
    private lateinit var promisePlaceName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promise_add3)

        promiseBack3Iv = findViewById(R.id.promiseBack3IV)
        promiseDone3Tv = findViewById(R.id.promiseDone3TV)
        promiseSelectPlaceTv = findViewById(R.id.promiseSelectPlaceTV)
        promisePlaceTv = findViewById(R.id.promisePlaceTV)
        promisePlaceSearchEt = findViewById(R.id.promisePlaceSearchET)
        promisePlaceSearchBtn = findViewById(R.id.promisePlaceSearchBTN)
        promisePlaceSearchRv = findViewById(R.id.promisePlaceSearchRV)

        promiseBack3Iv.setOnClickListener {
            finish()
        }

        promisePlaceSearchRv.layoutManager = LinearLayoutManager(this@PromiseAdd3Activity, LinearLayoutManager.VERTICAL, false)
        promisePlaceSearchRv.adapter = listAdapter
        listAdapter.setItemClickListener(object: PromisePlaceAdapter.OnItemClickListener{
            @SuppressLint("SetTextI18n")
            override fun onClick(v: View, position: Int) {
                promiseLatitude = listItems[position].x
                promiseLongitude = listItems[position].y
                promisePlaceName = listItems[position].name
                promisePlaceTv.visibility = View.VISIBLE
                promiseSelectPlaceTv.text = " ${listItems[position].name}"
            }

        })

        promisePlaceSearchBtn.setOnClickListener {
            keyword = promisePlaceSearchEt.text.toString()
            searchKeyword(keyword)
        }

        promiseDone3Tv.setOnClickListener {
            if(promisePlaceTv.visibility == View.VISIBLE) {
                if(promiseLatitude != null && promiseLongitude != null && promisePlaceName.isNotBlank()) {
                    val intent = intent
                    val promiseName = intent.getStringExtra("promiseName").toString()
                    val promiseDate = intent.getStringExtra("promiseDate").toString()
                    val promiseTime = intent.getStringExtra("promiseTime").toString()
                    val promiseCheck = intent.getSerializableExtra("promiseCheck").toString()

                    val myIntent = Intent(this@PromiseAdd3Activity, PromiseAdd4Activity::class.java)
                    myIntent.putExtra("promiseName", promiseName)
                    myIntent.putExtra("promiseDate", promiseDate)
                    myIntent.putExtra("promiseTime", promiseTime)
                    myIntent.putExtra("promiseCheck", promiseCheck)
                    myIntent.putExtra("promiseLatitude", promiseLatitude)
                    myIntent.putExtra("promiseLongitude", promiseLongitude)
                    myIntent.putExtra("promisePlaceName", promisePlaceName)
                    startActivity(myIntent)
                    finish()
                }
            }
            else {
                ToastMessage.show(this@PromiseAdd3Activity, "장소를 선택해주세요")
            }
        }
    }

    private fun searchKeyword(keyword: String) {
        val callPost = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = callPost.create(KakaoAPI::class.java)
        val call = api.getSearchKeyword(API_KEY, keyword)

        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
                addItems(response.body())
            }
            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addItems(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            listItems.clear()
            for (document in searchResult!!.documents) {
                val item = PromisePlaceItem(document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble())
                listItems.add(item)
            }
            listAdapter.notifyDataSetChanged()

        } else {
            ToastMessage.show(this@PromiseAdd3Activity, "검색 결과가 없습니다")
        }
    }
}