package com.project.wheredu

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.project.wheredu.utility.Service
import com.project.wheredu.utility.ToastMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventInfoActivity : AppCompatActivity() {

    private lateinit var eventInfoBackIv: ImageView
    private lateinit var eventInfoNameTv: TextView
    private lateinit var eventInfoStartDateTv: TextView
    private lateinit var eventInfoEndDateTv: TextView
    private lateinit var eventInfoContentTv: TextView

    private val service = Service.getService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info)

        eventInfoBackIv = findViewById(R.id.eventInfoBackIv)
        eventInfoNameTv = findViewById(R.id.eventInfoNameTV)
        eventInfoStartDateTv = findViewById(R.id.eventInfoStartDateTV)
        eventInfoEndDateTv = findViewById(R.id.eventInfoEndDateTV)
        eventInfoContentTv = findViewById(R.id.eventInfoContentTV)

        eventInfoBackIv.setOnClickListener {
            finish()
        }

        val eventTitle = intent.getStringExtra("eventTitle").toString()
        eventInfoData(eventTitle)
    }

    private fun eventInfoData(eventTitle: String) {
        val callPost = service.eventInfoData(eventTitle)
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful) {
                    val result = response.body()!!.toString()
                    replaceData(result)
                }
                else {
                    ToastMessage.show(this@EventInfoActivity, "오류가 발생했습니다")
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                ToastMessage.show(this@EventInfoActivity, "서버 연결에 오류가 발생했습니다")
            }
        })
    }

    private fun replaceData(result: String) {
        val replace = result.replace("[","").replace("{","").replace("\"","").replace("}","").replace("]","")
            .replace("eventTitle:","").replace("eventStartDate:","").replace("eventEndDate:","").replace("eventContent:", "")
        val textSplit = replace.split(",")

        eventInfoNameTv.text =  textSplit[0]
        eventInfoStartDateTv.text =  textSplit[1]
        eventInfoEndDateTv.text =  textSplit[2]
        eventInfoContentTv.text =  textSplit[3]
    }
}