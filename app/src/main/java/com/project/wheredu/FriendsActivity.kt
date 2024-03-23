package com.project.wheredu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.wheredu.dialog.CustomFriendAddDialogAdapter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class FriendsActivity : AppCompatActivity() {

    private lateinit var friendsAddIv: ImageView
    private lateinit var friendUserProfileIv: ImageView
    private lateinit var friendUserNicknameTv: TextView
    private lateinit var friendsBottomNav: BottomNavigationView

    private lateinit var preferences: SharedPreferences

    private val service = Service.getService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        friendUserProfileIv = findViewById(R.id.friendUserProfileIV)
        friendUserNicknameTv = findViewById(R.id.friendUserNicknameTV)
        friendsAddIv = findViewById(R.id.friendsAddIv)
        friendsBottomNav = findViewById(R.id.friends_bottomNav)

        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        val storeID = preferences.getString("accountID", "").toString()
        returnNickname2(storeID)

        friendsAddIv.setOnClickListener {
            val dlg = CustomFriendAddDialogAdapter(this@FriendsActivity)
            dlg.setOnAcceptClickedListener { friendNickname ->
                returnNickname(storeID, friendNickname)
            }
            dlg.show()
        }

        friendsBottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.main_promise -> {
                    startActivity(Intent(this@FriendsActivity, PromiseActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }
                R.id.main_home -> {
                    startActivity(Intent(this@FriendsActivity, MainActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }
                R.id.main_mypage -> {
                    startActivity(Intent(this@FriendsActivity, MyPageActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener  true
                }
                else -> {
                    return@setOnItemSelectedListener true
                }
            }
        }
    }

    private fun returnNickname2(userID: String) {
        val callPost = service.getUserData(userID)
        callPost.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        friendUserNicknameTv.text = result
                        downloadImage(result)
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@FriendsActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@FriendsActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun returnNickname(userID: String, friendNickname: String) {
        val callPost = service.getUserData(userID)
        callPost.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result != friendNickname) {
                            friendAdd(result, friendNickname)
                        }
                        else {
                            Toast.makeText(this@FriendsActivity, "자기 자신은 영원한 친구입니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@FriendsActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@FriendsActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
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
                        friendUserProfileIv.setImageBitmap(bitmap)
                    }
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("MyPageActivity", "Image download failed: ${t.message}")
            }
        })
    }

    private fun friendAdd(userNickname: String, friendNickname: String) {
        val callPost = service.friendAdd(userNickname, friendNickname)
        callPost.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result == "pass") {
                            Toast.makeText(this@FriendsActivity,"친구 추가가 완료되었습니다", Toast.LENGTH_SHORT).show()
                        }
                        if(result == "nicknameFail"){
                            Toast.makeText(this@FriendsActivity,"친구 닉네임이 틀립니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@FriendsActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@FriendsActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }
}