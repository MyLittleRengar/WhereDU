package com.project.wheredu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.wheredu.friend.FriendsActivity
import com.project.wheredu.promise.PromiseActivity
import com.project.wheredu.promise.PromiseAdd1Activity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var promiseAddIv: ImageView

    private lateinit var mainBottomNav: BottomNavigationView

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val service = Service.getService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        promiseAddIv = findViewById(R.id.promiseAddIv)

        val distance = Distance.calculateAndFormatDistance(35.9124703, 128.8188155, 35.9026591, 128.8563364)

        Log.d("Distance", distance)

        mainBottomNav = findViewById(R.id.main_bottomNav)

        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        val storeID = preferences.getString("accountID", "").toString()
        val storeNick = preferences.getString("accountNickname", "").toString()
        if(storeNick == "") {
            returnNickname(storeID)
        }

        promiseAddIv.setOnClickListener {
            startActivity(Intent(this@MainActivity, PromiseAdd1Activity::class.java))
            finish()
        }

        mainBottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.main_promise -> {
                    startActivity(Intent(this@MainActivity, PromiseActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }
                R.id.main_friend -> {
                    startActivity(Intent(this@MainActivity, FriendsActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }
                R.id.main_mypage -> {
                    startActivity(Intent(this@MainActivity, MyPageActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }

                else -> {
                    return@setOnItemSelectedListener true
                }
            }
        }
    }

    private fun returnNickname(userID: String) {
        val callPost = service.getUserData(userID)
        callPost.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        editor = preferences.edit()
                        editor.putString("accountNickname", result)
                        editor.apply()
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@MainActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@MainActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }
}