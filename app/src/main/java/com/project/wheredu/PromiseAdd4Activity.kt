package com.project.wheredu

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PromiseAdd4Activity : AppCompatActivity() {

    private lateinit var promiseBack4Iv: ImageView
    private lateinit var promiseDone4Tv: TextView
    private lateinit var promiseNameTv: TextView
    private lateinit var promiseDateTimeTv: TextView
    private lateinit var promiseMemoEt: EditText
    private lateinit var promisePlaceBtn: Button
    private lateinit var promiseFriendBtn: Button
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

        val promiseName = intent.getStringExtra("promiseName").toString()
        val promiseDate = intent.getStringExtra("promiseDate").toString()
        val promiseTime = intent.getStringExtra("promiseTime").toString()
        val promiseCheck = intent.getSerializableExtra("promiseCheck").toString()
        val promiseLatitude = intent.getDoubleExtra("promiseLatitude", 0.0).toString()
        val promiseLongitude = intent.getDoubleExtra("promiseLongitude", 0.0).toString()
        val promisePlaceName = intent.getStringExtra("promisePlaceName")

        promiseNameTv.text = promiseName
        promiseDateTimeTv.text = "날짜: $promiseDate, 시간: $promiseTime"
        promisePlaceBtn.text = promisePlaceName
        val checkReplace = promiseCheck.replace("=true", "").replace("[","").replace("]", "")
        val checkSplit = checkReplace.split(",").sortedDescending()
        if(checkSplit.size >= 2) {
            promiseFriendBtn.text = "${checkSplit[0]}외 ${checkSplit.size-1}명"
        }
        else {
            promiseFriendBtn.text = checkSplit[0]
        }


        promiseDone4Tv.setOnClickListener {

        }

        promiseBack4Iv.setOnClickListener {
            finish()
        }
    }
}