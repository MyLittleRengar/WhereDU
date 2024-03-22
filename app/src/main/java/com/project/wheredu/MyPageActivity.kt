package com.project.wheredu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MyPageActivity : AppCompatActivity() {

    private lateinit var myPageSettingIv: ImageView
    private lateinit var myPageUserIdTv: TextView
    private lateinit var myPageUserNicknameTv: TextView
    private lateinit var myPageUserProfileImgIv: ImageView
    private lateinit var myPageEventLl: LinearLayout
    private lateinit var myPageNoticeLl: LinearLayout
    private lateinit var myPageInquiryLl: LinearLayout

    private lateinit var myPageBottomNav: BottomNavigationView

    private lateinit var preferences: SharedPreferences
    private val service = Service.getService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)

        myPageSettingIv = findViewById(R.id.myPageSettingIv)
        myPageUserIdTv = findViewById(R.id.myPage_UserIDTV)
        myPageUserNicknameTv = findViewById(R.id.myPage_UserNickNameTV)
        myPageUserProfileImgIv = findViewById(R.id.myPage_UserProfileImgIV)
        myPageEventLl = findViewById(R.id.myPageEventLL)
        myPageNoticeLl = findViewById(R.id.myPageNoticeLL)
        myPageInquiryLl = findViewById(R.id.myPageInquiryLL)
        myPageBottomNav = findViewById(R.id.myPage_bottomNav)

        val saveID = preferences.getString("accountID", "").toString()
        getUserData(saveID)

        myPageSettingIv.setOnClickListener {
            startActivity(Intent(this@MyPageActivity, SettingActivity::class.java))
        }
        myPageEventLl.setOnClickListener {
            val intent = Intent(this@MyPageActivity, EventActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_exit)
        }
        myPageNoticeLl.setOnClickListener {
            val intent = Intent(this@MyPageActivity, NoticeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_exit)
        }
        myPageInquiryLl.setOnClickListener {
            val intent = Intent(this@MyPageActivity, InquiryActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_exit)
        }

        myPageBottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.main_promise -> {
                    startActivity(Intent(this@MyPageActivity, PromiseActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }
                R.id.main_friend -> {
                    startActivity(Intent(this@MyPageActivity, FriendsActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }
                R.id.main_home -> {
                    startActivity(Intent(this@MyPageActivity, MainActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }

                else -> {
                    return@setOnItemSelectedListener true
                }
            }
        }
    }

    private fun getUserData(userID: String) {
        val callPost = service.getUserData(userID)
        callPost.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        myPageUserNicknameTv.text = result
                        myPageUserIdTv.text = userID
                        downloadImage(result)
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@MyPageActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@MyPageActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun downloadImage(userNickname: String) {
        val call = service.downloadImage(userNickname)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val imageBytes = response.body()?.bytes()
                    imageBytes?.let {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        myPageUserProfileImgIv.setImageBitmap(bitmap)
                    }
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("MyPageActivity", "Image download failed: ${t.message}")
            }
        })
    }
}