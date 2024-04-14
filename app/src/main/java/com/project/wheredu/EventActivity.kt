package com.project.wheredu

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout

class EventActivity : AppCompatActivity() {

    private lateinit var eventTl: TabLayout
    private lateinit var eventLl: LinearLayout
    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        eventTl = findViewById(R.id.eventTL)
        eventLl = findViewById(R.id.eventLL)

        eventLl.addView(LayoutInflater.from(this@EventActivity).inflate(R.layout.activity_now_event, null))

        eventTl.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when(tab.position) {
                    0 -> {
                        val newLayout = LayoutInflater.from(this@EventActivity).inflate(R.layout.activity_now_event, null)
                        eventLl.removeAllViews()
                        eventLl.addView(newLayout)
                    }
                    1 -> {
                        val newLayout = LayoutInflater.from(this@EventActivity).inflate(R.layout.activity_past_event, null)
                        eventLl.removeAllViews()
                        eventLl.addView(newLayout)
                    }
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab) {}
            override fun onTabReselected(p0: TabLayout.Tab) {}
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this@EventActivity, MyPageActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
        super.onBackPressed()
        finish()
    }
}