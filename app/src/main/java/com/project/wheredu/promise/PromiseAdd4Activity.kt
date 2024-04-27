package com.project.wheredu.promise

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.project.wheredu.R
import com.project.wheredu.utility.Service
import com.project.wheredu.utility.ToastMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class PromiseAdd4Activity : AppCompatActivity() {

    private lateinit var promiseBack4Iv: ImageView
    private lateinit var promiseDone4Tv: TextView
    private lateinit var promiseNameTv: TextView
    private lateinit var promiseDateTimeTv: TextView
    private lateinit var promiseMemoEt: EditText
    private lateinit var promisePlaceBtn: Button
    private lateinit var promiseFriendBtn: Button

    private lateinit var preferences: SharedPreferences

    private val service = Service.getService()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promise_add4)

        promiseBack4Iv = findViewById(R.id.promiseBack4IV)
        promiseDone4Tv = findViewById(R.id.promiseDone4TV)
        promiseNameTv = findViewById(R.id.promiseNameTV)
        promiseDateTimeTv = findViewById(R.id.promiseDateTimeTV)
        promiseMemoEt = findViewById(R.id.promiseMemoET)
        promisePlaceBtn = findViewById(R.id.promisePlaceBTN)
        promiseFriendBtn = findViewById(R.id.promiseFriendBTN)

        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        val storeNick = preferences.getString("accountNickname", "").toString()

        val promiseName = intent.getStringExtra("promiseName").toString()
        val promiseDate = intent.getStringExtra("promiseDate").toString()
        val promiseTime = intent.getStringExtra("promiseTime").toString()
        val promiseCheck = intent.getSerializableExtra("promiseCheck").toString()
        val promiseLatitude = intent.getDoubleExtra("promiseLatitude", 0.0)
        val promiseLongitude = intent.getDoubleExtra("promiseLongitude", 0.0)
        val promisePlaceName = intent.getStringExtra("promisePlaceName").toString()
        val promisePlaceDetail = intent.getStringExtra("promisePlaceDetail").toString()

        promiseNameTv.text = promiseName
        promiseDateTimeTv.text = "날짜: $promiseDate, 시간: $promiseTime"
        promisePlaceBtn.text = promisePlaceName
        val checkReplace = promiseCheck.replace("=true", "").replace("[","").replace("]", "")
        val checkSplit = checkReplace.split(",")
            .map { it.trim() }
            .sorted()
            .filter { !it.endsWith("=false")  }
        if(checkSplit.size >= 2) {
            promiseFriendBtn.text = "${checkSplit[0]}외 ${checkSplit.size-1}명"
        }
        else {
            promiseFriendBtn.text = checkSplit[0]
        }


        promiseDone4Tv.setOnClickListener {
            val promiseMemo = promiseMemoEt.text.toString()
            addPromise(storeNick, promiseName, promiseLatitude, promiseLongitude, promisePlaceName, promisePlaceDetail,promiseDate, promiseTime, checkSplit, promiseMemo)
        }

        promiseBack4Iv.setOnClickListener {
            finish()
        }
    }

    private fun addPromise(owner: String,name: String, latitude: Double, longitude: Double, place: String, detail: String, date: String, time: String, member: List<String>, memo: String) {
        val callPost = service.addPromise(owner, name, latitude, longitude, place, detail, date, time, member, memo)
        callPost.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result == "pass") {
                            ToastMessage.show(this@PromiseAdd4Activity, "약속이 추가되었습니다")
                            startActivity(Intent(this@PromiseAdd4Activity, PromiseActivity::class.java))
                            ActivityCompat.finishAffinity(this@PromiseAdd4Activity)
                        }
                        else if(result == "nameFail") {
                            ToastMessage.show(this@PromiseAdd4Activity, "동일한 이름의 약속이 있습니다")
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@PromiseAdd4Activity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@PromiseAdd4Activity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }
}