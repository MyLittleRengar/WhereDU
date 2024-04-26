package com.project.wheredu.promise

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.wheredu.R
import com.project.wheredu.utility.Service
import com.project.wheredu.utility.ToastMessage
import com.project.wheredu.recycler.CheckItem
import com.project.wheredu.recycler.FriendItem
import com.project.wheredu.recycler.PromiseFriendAdapter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.Serializable


class PromiseAdd2Activity : AppCompatActivity(), PromiseFriendAdapter.CheckBoxStateListener {

    private lateinit var promiseBack2Iv: ImageView
    private lateinit var promiseDone2Tv: TextView
    private lateinit var promiseFriendRv: RecyclerView
    private var datas = mutableListOf<FriendItem>()
    private var checkData = mutableSetOf<CheckItem>()

    private lateinit var preferences: SharedPreferences
    private lateinit var friendAdapter: PromiseFriendAdapter
    private val service = Service.getService()
    private lateinit var storeNick: String

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promise_add2)

        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        storeNick = preferences.getString("accountNickname", "").toString()
        friendListDataCount(storeNick)

        promiseBack2Iv = findViewById(R.id.promiseBack2IV)
        promiseDone2Tv = findViewById(R.id.promiseDone2TV)
        promiseFriendRv = findViewById(R.id.promiseFriendRV)

        promiseBack2Iv.setOnClickListener {
            finish()
        }

        promiseDone2Tv.setOnClickListener {
            if(checkData.isNotEmpty()) {
                val myIntent = Intent(this@PromiseAdd2Activity, PromiseAdd3Activity::class.java)
                val promiseName = intent.getStringExtra("promiseName").toString()
                val promiseDate = intent.getStringExtra("promiseDate").toString()
                val promiseTime = intent.getStringExtra("promiseTime").toString()
                myIntent.putExtra("promiseName", promiseName)
                myIntent.putExtra("promiseDate", promiseDate)
                myIntent.putExtra("promiseTime", promiseTime)
                myIntent.putExtra("promiseCheck", checkData as Serializable)
                startActivity(myIntent)
                finish()
            }
            else {
                ToastMessage.show(this@PromiseAdd2Activity, "약속 친구를 체크해주세요")
            }
        }
    }

    override fun onCheckBoxStateChanged(position: Int, isChecked: Boolean, nickname: String) {
        val existingItem = checkData.find { it.nickname == nickname }
         if (existingItem != null) {
            if (existingItem.check != isChecked) {
                checkData.remove(existingItem)
                checkData.add(existingItem.copy(check = isChecked))
            }
        } else {
            checkData.add(CheckItem(nickname, isChecked))
        }
    }

    private fun loopInt(data: Int, userNickname:String) {
        for(i in 0 until data) {
            friendListData(userNickname, i)
            if(i == 0) {
                datas.clear()
            }
        }
    }
    private fun friendListDataCount(userNickname: String) {
        val callPost = service.returnPromiseFriendCount(userNickname)
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toInt()
                        loopInt(result, userNickname)
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@PromiseAdd2Activity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@PromiseAdd2Activity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }
    private fun friendListData(userNickname: String, userInt: Int) {
        val callPost = service.promiseFriendListData(userNickname, userInt)
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        downloadImage2(result)
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@PromiseAdd2Activity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@PromiseAdd2Activity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }
    private fun downloadImage2(userNickname: String) {
        val call = service.downloadImage(userNickname)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val imageBytes = response.body()?.bytes()
                    imageBytes?.let {
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        initRecycler(bitmap, userNickname)
                    }
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("MyPageActivity", "Image download failed: ${t.message}")
            }
        })
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycler(img: Bitmap, nickname: String) {
        friendAdapter = PromiseFriendAdapter(this)
        friendAdapter.listener = this
        promiseFriendRv.adapter = friendAdapter

        datas.apply {
            add(FriendItem(img, nickname))
        }
        promiseFriendRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        promiseFriendRv.setHasFixedSize(true)
        datas.sortBy { it.nickname }
        friendAdapter.datas = datas
        friendAdapter.notifyDataSetChanged()
    }
}