package com.project.wheredu

import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class PromiseAdd1Activity : AppCompatActivity() {

    private lateinit var promiseBack1Iv: ImageView
    private lateinit var promiseDoneTV: TextView
    private lateinit var promisePromiseNameET: EditText
    private lateinit var promiseAddDP: DatePicker
    private lateinit var promisePromiseTimeET: EditText

    private var pickDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promise_add1)

        promiseBack1Iv = findViewById(R.id.promiseBack1Iv)
        promiseDoneTV = findViewById(R.id.promiseDoneTV)
        promisePromiseNameET = findViewById(R.id.promisePromiseNameET)
        promiseAddDP = findViewById(R.id.promiseAddDP)
        promisePromiseTimeET = findViewById(R.id.promisePromiseTimeET)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        promiseAddDP.updateDate(year, month, day)

        promiseDoneTV.setOnClickListener {
            if(promisePromiseNameET.text.isNotBlank()) {
                if(pickDate!="") {
                    if(promisePromiseTimeET.text.isNotBlank()) {
                        val promiseName = promisePromiseNameET.text.toString()
                        val promiseTime = promisePromiseTimeET.text.toString()
                        val intent = Intent(this@PromiseAdd1Activity, PromiseAdd2Activity::class.java)
                        intent.putExtra("promiseName", promiseName)
                        intent.putExtra("promiseDate", pickDate)
                        intent.putExtra("promiseTime", promiseTime)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        ToastMessage.show(this@PromiseAdd1Activity, "약속 시간을 설정해주세요")
                    }
                }
                else {
                    ToastMessage.show(this@PromiseAdd1Activity, "약속 날짜를 설정해주세요")
                }
            }
            else {
                ToastMessage.show(this@PromiseAdd1Activity, "약속 이름을 입력해주세요")
            }
        }

        promiseAddDP.setOnDateChangedListener { _, year, month, day ->
            pickDate = "$year.${month + 1}.$day"
        }

        promiseBack1Iv.setOnClickListener {
            finish()
        }
    }
}