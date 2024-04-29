package com.project.wheredu.board

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.project.wheredu.R
import com.project.wheredu.utility.Service
import com.project.wheredu.utility.ToastMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeInfoActivity : AppCompatActivity() {

    private lateinit var noticeInfoBackIv: ImageView
    private lateinit var noticeInfoNameTv: TextView
    private lateinit var noticeInfoDateTv: TextView
    private lateinit var noticeInfoContentTv: TextView

    private val service = Service.getService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_info)

        val noticeTitle = intent.getStringExtra("noticeName").toString()

        noticeInfoBackIv = findViewById(R.id.noticeInfoBackIv)
        noticeInfoNameTv = findViewById(R.id.noticeInfoNameTV)
        noticeInfoDateTv = findViewById(R.id.noticeInfoDateTV)
        noticeInfoContentTv = findViewById(R.id.noticeInfoContentTV)

        noticeInfoData(noticeTitle)

        noticeInfoBackIv.setOnClickListener {
            finish()
        }
    }

    private fun noticeInfoData(eventTitle: String) {
        val callPost = service.noticeInfoData(eventTitle)
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful) {
                    val result = response.body()!!.toString()
                    replaceData(result)
                }
                else {
                    ToastMessage.show(this@NoticeInfoActivity, "오류가 발생했습니다")
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                ToastMessage.show(this@NoticeInfoActivity, "서버 연결에 오류가 발생했습니다")
            }
        })
    }

    private fun replaceData(result: String) {
        val replace = result.replace("[","").replace("{","").replace("\"","").replace("}","").replace("]","")
            .replace("noticeTitle:","").replace("noticeDate:","").replace("noticeContent:", "")
        val textSplit = replace.split(",")

        noticeInfoNameTv.text =  textSplit[0]
        noticeInfoDateTv.text =  textSplit[1]
        noticeInfoContentTv.text =  textSplit[2]
    }
}