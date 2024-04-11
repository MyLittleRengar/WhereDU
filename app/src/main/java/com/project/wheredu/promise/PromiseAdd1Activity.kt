package com.project.wheredu.promise

import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.project.wheredu.R
import com.project.wheredu.ToastMessage
import java.util.Calendar

class PromiseAdd1Activity : AppCompatActivity() {

    private lateinit var promiseBack1Iv: ImageView
    private lateinit var promiseDoneTV: TextView
    private lateinit var promisePromiseNameET: EditText
    private lateinit var promiseAddDP: DatePicker
    private lateinit var promisePromiseTimeTp: TimePicker

    private var pickDate = ""
    private var pickTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promise_add1)

        promiseBack1Iv = findViewById(R.id.promiseBack1Iv)
        promiseDoneTV = findViewById(R.id.promiseDoneTV)
        promisePromiseNameET = findViewById(R.id.promisePromiseNameET)
        promiseAddDP = findViewById(R.id.promiseAddDP)
        promisePromiseTimeTp = findViewById(R.id.promisePromiseTimeTP)

        val calendar = Calendar.getInstance()
        val nowYear = calendar.get(Calendar.YEAR)
        val nowMonth = calendar.get(Calendar.MONTH)
        val nowDay = calendar.get(Calendar.DAY_OF_MONTH)

        promiseAddDP.updateDate(nowYear, nowMonth, nowDay)

        promiseDoneTV.setOnClickListener {
            if(promisePromiseNameET.text.isNotBlank()) {
                if(pickDate!="") {
                    if(pickTime!="") {
                        val promiseName = promisePromiseNameET.text.toString()
                        val intent = Intent(this@PromiseAdd1Activity, PromiseAdd2Activity::class.java)
                        intent.putExtra("promiseName", promiseName)
                        intent.putExtra("promiseDate", pickDate)
                        intent.putExtra("promiseTime", pickTime)
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

        promisePromiseTimeTp.setOnTimeChangedListener { _, hour, minute ->
            pickTime = "$hour:$minute"
        }

        promiseBack1Iv.setOnClickListener {
            finish()
        }
    }
}