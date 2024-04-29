package com.project.wheredu.board

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.project.wheredu.MyPageActivity
import com.project.wheredu.R
import com.project.wheredu.utility.Service
import com.project.wheredu.utility.ToastMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InquiryActivity : AppCompatActivity() {

    private lateinit var inquiryBackIv: ImageView
    private lateinit var inquiryNicknameTv: TextView
    private lateinit var inquiryContentEt: EditText
    private lateinit var inquiryBtn: Button

    private lateinit var preferences: SharedPreferences
    private val service = Service.getService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inquiry)

        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        val storeNick = preferences.getString("accountNickname", "").toString()

        inquiryBackIv = findViewById(R.id.inquiryBackIV)
        inquiryNicknameTv = findViewById(R.id.inquiryNicknameTV)
        inquiryContentEt = findViewById(R.id.inquiryContentET)
        inquiryBtn = findViewById(R.id.inquiryBTN)

        inquiryNicknameTv.text = storeNick

        inquiryBtn.setOnClickListener {
            if(inquiryContentEt.text.isNotBlank()) {
                inquiryText(storeNick, inquiryContentEt.text.toString())
            }
            else {
                ToastMessage.show(this@InquiryActivity, "문의 내용을 입력해주세요")
            }
        }

        inquiryBackIv.setOnClickListener {
            val intent = Intent(this@InquiryActivity, MyPageActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
            finish()
        }
    }

    private fun inquiryText(name: String, content: String) {
        val dateFormat = SimpleDateFormat("yyyy.M.dd", Locale.KOREA)
        val currentDate = dateFormat.format(Date())
        val callPost = service.inquiry(name, content, currentDate)
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    val result = response.body()!!.toString()
                    if(result == "pass") {
                        ToastMessage.show(this@InquiryActivity, "문의가 접수되었습니다")
                    }
                }
                else {
                    ToastMessage.show(this@InquiryActivity, "오류가 발생했습니다")
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                ToastMessage.show(this@InquiryActivity, "서버 연결에 오류가 발생했습니다")
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this@InquiryActivity, MyPageActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
        super.onBackPressed()
        finish()
    }
}