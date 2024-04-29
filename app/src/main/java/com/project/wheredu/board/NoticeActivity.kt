package com.project.wheredu.board

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.wheredu.MyPageActivity
import com.project.wheredu.R
import com.project.wheredu.recycler.NoticeItem
import com.project.wheredu.recycler.NoticeListAdapter
import com.project.wheredu.utility.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class NoticeActivity : AppCompatActivity() {

    private lateinit var noticeBackIv: ImageView
    private lateinit var noticeRv: RecyclerView

    private var datas = mutableListOf<NoticeItem>()
    private lateinit var noticeListAdapter: NoticeListAdapter
    private val service = Service.getService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)

        noticeBackIv = findViewById(R.id.noticeBackIv)
        noticeRv = findViewById(R.id.noticeRv)

        returnNoticeData()

        noticeBackIv.setOnClickListener {
            finish()
        }
    }

    private fun returnNoticeData() {
        val callPost = service.returnNoticeData("")
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body().toString()
                        if(result != "noData") {
                            loopInt(result.toInt())
                        }
                        else {
                            datas.clear()
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@NoticeActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@NoticeActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun noticeData(pos: Int) {
        val callPost = service.noticeData(pos)
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result != "noData") {
                            replaceData(result)
                        }
                        else {
                            datas.clear()
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this@NoticeActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@NoticeActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun replaceData(result: String) {
        val replace = result.replace("[","").replace("{","").replace("\"","").replace("}","").replace("]","")
            .replace("noticeTitle:","").replace("noticeDate:","")
        val textSplit = replace.split(",")
        initRecycler(textSplit[0], textSplit[1])
    }

    private fun loopInt(data: Int) {
        for(i in 0 until data) {
            noticeData(i)
            if(i == 0) {
                datas.clear()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycler(name: String, date: String) {
        noticeListAdapter = NoticeListAdapter(this)
        noticeRv.adapter = noticeListAdapter

        datas.apply {
            add(NoticeItem(name, date))
        }
        noticeRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        noticeRv.setHasFixedSize(true)
        noticeListAdapter.datas = datas
        noticeListAdapter.notifyDataSetChanged()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this@NoticeActivity, MyPageActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
        super.onBackPressed()
        finish()
    }
}