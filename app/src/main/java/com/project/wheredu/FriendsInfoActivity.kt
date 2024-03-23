package com.project.wheredu

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FriendsInfoActivity : AppCompatActivity() {

    private lateinit var friendsProfileIv: CircleImageView
    private val service = Service.getService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_info)

        friendsProfileIv = findViewById(R.id.friends_profileIV)
    }

    private fun downloadImage(selectUserNickname: String) {
        val call = service.downloadImage(selectUserNickname)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val imageBytes = response.body()?.bytes()
                    imageBytes?.let {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        friendsProfileIv.setImageBitmap(bitmap)
                    }
                } else {
                    Log.e("SSSSSSSSSSS", "다운로드 실패")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("SSSSSSSSSSS", "Image download failed: ${t.message}")
            }
        })
    }
}