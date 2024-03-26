package com.project.wheredu

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.wheredu.dialog.CustomFriendDeleteDialogAdapter
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class FriendsInfoActivity : AppCompatActivity() {

    private lateinit var friendsProfileIv: CircleImageView
    private lateinit var friendsUserNicknameTv: TextView
    private lateinit var friendsInfoBackIv: ImageView
    private lateinit var friendsInfoBookmarkIv: ImageView
    private lateinit var friendsInfoDeleteIv: ImageView

    private lateinit var preferences: SharedPreferences
    private val service = Service.getService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_info)

        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)

        friendsProfileIv = findViewById(R.id.friends_profileIV)
        friendsUserNicknameTv = findViewById(R.id.friends_UserNicknameTV)
        friendsInfoBackIv = findViewById(R.id.friendsInfoBackIV)
        friendsInfoBookmarkIv = findViewById(R.id.friendsInfoBookmarkIV)
        friendsInfoDeleteIv = findViewById(R.id.friendsInfoDeleteIV)

        val intent = intent
        val userNick = intent.getStringExtra("userNick")

        friendsUserNicknameTv.text = userNick
        getUserProfile(userNick!!)

        friendsInfoBackIv.setOnClickListener {
            finish()
        }

        friendsInfoBookmarkIv.setOnClickListener {

        }
        friendsInfoDeleteIv.setOnClickListener {
            val dlg = CustomFriendDeleteDialogAdapter(this@FriendsInfoActivity)
            dlg.setOnAcceptClickedListener { content ->
                if(content == "pass") {
                    friendDelete(userNick)
                }
            }
            dlg.show(userNick)
        }
    }

    private fun friendDelete(userNickname: String) {
        val storeNick = preferences.getString("accountNickname", "").toString()
        val callPost = service.friendDelete(userNickname, storeNick)
        callPost.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result == "pass") {
                            Toast.makeText(this@FriendsInfoActivity, "친구 삭제가 완료되었습니다", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@FriendsInfoActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@FriendsInfoActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }

        })
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