package com.project.wheredu

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.wheredu.utility.Service
import com.project.wheredu.recycler.EventItem
import com.project.wheredu.recycler.EventListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class PastEventActivity : AppCompatActivity() {

    private lateinit var pastEventRv: RecyclerView

    private var datas = mutableListOf<EventItem>()
    private lateinit var eventListAdapter: EventListAdapter
    private val service = Service.getService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_event)

        pastEventRv = findViewById(R.id.pastEventRV)

        returnEventData()
    }

    private fun returnEventData() {
        val callPost = service.returnEventData("")
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toInt()
                        loopInt(result)
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@PastEventActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@PastEventActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun eventData(pos: Int) {
        val callPost = service.eventData(pos)
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
                    Toast.makeText(this@PastEventActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@PastEventActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun replaceData(result: String) {
        val replace = result.replace("[","").replace("{","").replace("\"","").replace("}","").replace("]","")
            .replace("eventName:","").replace("startDate:","").replace("endDate:","")
        val textSplit = replace.split(",")
        initRecycler(textSplit[0], textSplit[1], textSplit[2])
    }

    private fun loopInt(data: Int) {
        for(i in 0 until data) {
            eventData(i)
            if(i == 0) {
                datas.clear()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycler(name: String, start: String, end: String) {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        val endString = LocalDate.parse(end, formatter)
        val daysUntilEnd = ChronoUnit.DAYS.between(today, endString)
        val dDay =
            if (daysUntilEnd > 0) {
                "D-${daysUntilEnd}"
            } else if (daysUntilEnd == 0L) {
                "D-Day"
            } else {
                "종료"
            }
        if(dDay == "종료") {
            eventListAdapter = EventListAdapter(this)
            pastEventRv.adapter = eventListAdapter

            datas.apply {
                add(EventItem(name, start, end))
            }
            pastEventRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            pastEventRv.setHasFixedSize(true)
            eventListAdapter.datas = datas
            eventListAdapter.notifyDataSetChanged()
        }
    }
}