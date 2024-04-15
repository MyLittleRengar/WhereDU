package com.project.wheredu

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.wheredu.api.Service
import com.project.wheredu.recycler.EventItem
import com.project.wheredu.recycler.EventListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class NowEventActivity : AppCompatActivity() {

    private lateinit var nowEventRv: RecyclerView

    private var listItems = arrayListOf<EventItem>()
    private  var eventListAdapter = EventListAdapter(listItems)

    private val service = Service.getService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_now_event)

        nowEventRv = findViewById(R.id.nowEventRV)

        nowEventRv.layoutManager = LinearLayoutManager(this@NowEventActivity, LinearLayoutManager.VERTICAL, false)
        nowEventRv.adapter = eventListAdapter
        eventListAdapter.setItemClickListener(object: EventListAdapter.OnItemClickListener {
            override fun onInfoClick(position: Int, name: String, start: String, end: String, day: String) {
                val intent = Intent(this@NowEventActivity, EventInfoActivity::class.java)
                intent.putExtra("eventName", name)
                intent.putExtra("eventStart", start)
                intent.putExtra("eventEnd", end)
                intent.putExtra("eventDay", day)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun eventData(event: String) {
        val callPost = service.eventData(event)
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        replaceData(result)

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this@NowEventActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@NowEventActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun replaceData(result: String) {
        val replace = result.replace("[","").replace("{","").replace("\"","").replace("}","").replace("]","")
            .replace("eventName:","").replace("startDate:","").replace("endDate:","")
        val textSplit = replace.split(",")
        listItems.clear()
        initRecycler(textSplit.size, textSplit)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycler(num: Int, data: List<String>) {
        for(i in 0 until num) {
            //val item = EventItem(name, start, end)
            //listItems.add(item)
        }
        eventListAdapter.notifyDataSetChanged()
    }
}