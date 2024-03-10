package com.project.wheredu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class LoginActivity : AppCompatActivity() {

    private lateinit var idET: EditText
    private lateinit var pwET: EditText
    private lateinit var loginBTN: Button
    private lateinit var registerTV: TextView
    private lateinit var findTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        idET = findViewById(R.id.idET)
        pwET = findViewById(R.id.pwET)
        loginBTN = findViewById(R.id.loginBTN)
        registerTV = findViewById(R.id.registerTV)
        findTV = findViewById(R.id.findTV)
    }
}