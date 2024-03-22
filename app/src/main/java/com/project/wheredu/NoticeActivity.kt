package com.project.wheredu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NoticeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)
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