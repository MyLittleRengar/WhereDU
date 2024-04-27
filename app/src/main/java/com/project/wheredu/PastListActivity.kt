package com.project.wheredu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.wheredu.friend.FriendsActivity
import com.project.wheredu.promise.PromiseActivity
import com.project.wheredu.recycler.PastPromiseItem
import com.project.wheredu.recycler.PastPromiseListAdapter
import com.project.wheredu.utility.Service
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class PastListActivity : AppCompatActivity() {

    private lateinit var pastListRv: RecyclerView
    private lateinit var pastBottomNav: BottomNavigationView

    private var listItems = arrayListOf<PastPromiseItem>()
    private var pastPromiseListAdapter = PastPromiseListAdapter(listItems)

    private val service = Service.getService()
    private lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_list)

        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        val storeNick = preferences.getString("accountNickname", "").toString()

        returnPastPromise(storeNick)

        pastListRv = findViewById(R.id.pastListRv)
        pastBottomNav = findViewById(R.id.past_bottomNav)

        pastListRv.layoutManager = LinearLayoutManager(this@PastListActivity, LinearLayoutManager.VERTICAL, false)
        pastListRv.adapter = pastPromiseListAdapter


        pastBottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.main_home -> {
                    startActivity(Intent(this@PastListActivity, MainActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.main_promise -> {
                    startActivity(Intent(this@PastListActivity, PromiseActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.main_friend -> {
                    startActivity(Intent(this@PastListActivity, FriendsActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.main_mypage -> {
                    startActivity(Intent(this@PastListActivity, MyPageActivity::class.java))
                    finish()
                    return@setOnItemSelectedListener true
                }
                else -> {
                    return@setOnItemSelectedListener true
                }
            }
        }
    }

    private fun returnPastPromise(name: String) {
        val callPost = service.returnPastPromise(name)
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result != "noData") {
                            loopInt(name, result.toInt())
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@PastListActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@PastListActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loopInt(date: String, cnt: Int) {
        for(i in 0 until cnt) {
            pastPromise(date, i)
        }
    }

    private fun pastPromise(name: String, cnt: Int) {
        val callPost = service.pastPromise(name, cnt)
        callPost.enqueue(object: Callback<String> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result != "noData") {
                            selectReplaceData(result)
                        }
                        else {
                            listItems.clear()
                            pastPromiseListAdapter.notifyDataSetChanged()
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@PastListActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@PastListActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun selectReplaceData(result: String) {
        val jsonObject = JSONObject(result)

        val promiseName = jsonObject.getString("promiseName")
        val promiseMembers = jsonObject.getString("promiseMember")
        val promiseTime = jsonObject.getString("promiseTime")
        val promiseDate = jsonObject.getString("promiseDate")

        val membersList = promiseMembers.split(", ")

        val textSplit = listOf(promiseName, promiseDate, promiseTime, membersList.joinToString(", "))
        initRecycle(textSplit)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycle(data: List<String>) {
        if(data.isNotEmpty()) {
            val splitDate = data[1].split(".")
            val combineDateTime = "${splitDate[0]}년 ${splitDate[1]}월 ${splitDate[2]}일 ${data[2]}"
            val item = PastPromiseItem(name = data[0], friend = data[3], time = combineDateTime)
            Log.e("AAAA", item.toString())
            listItems.add(item)
            pastPromiseListAdapter.notifyDataSetChanged()
        }
    }
}