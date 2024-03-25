package com.project.wheredu

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FriendsInfoActivity : AppCompatActivity() {

    private lateinit var friendsProfileIv: CircleImageView
    private lateinit var friendsUserNicknameTv: TextView
    private lateinit var friendsInfoBackIv: ImageView

    private val service = Service.getService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_info)

        friendsProfileIv = findViewById(R.id.friends_profileIV)
        friendsUserNicknameTv = findViewById(R.id.friends_UserNicknameTV)
        friendsInfoBackIv = findViewById(R.id.friendsInfoBackIV)

        val intent = intent
        val userNick = intent.getStringExtra("userNick")

        friendsUserNicknameTv.text = userNick
        getUserProfile(userNick!!)

        friendsInfoBackIv.setOnClickListener {
            finish()
        }
    }

    private fun getUserProfile(userNickname: String) {
        val call = service.downloadImage(userNickname)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val imageBytes = response.body()?.bytes()
                    imageBytes?.let {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        friendsProfileIv.setImageBitmap(bitmap)
                    }
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("MyPageActivity", "Image download failed: ${t.message}")
            }
        })
    }
}