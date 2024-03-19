package com.project.wheredu.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.project.wheredu.R

class Register2Activity : AppCompatActivity() {

    private lateinit var registerNicknameEt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register2)
        val userID = intent.getStringExtra("userID")
        val userPW = intent.getStringExtra("userPW")
    }
}