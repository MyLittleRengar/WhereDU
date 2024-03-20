package com.project.wheredu.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.project.wheredu.MainActivity
import com.project.wheredu.R
import com.project.wheredu.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var idET: EditText
    private lateinit var pwET: EditText
    private lateinit var loginBTN: Button
    private lateinit var registerTV: TextView
    private lateinit var findTV: TextView

    private val service = Service.getService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        idET = findViewById(R.id.idET)
        pwET = findViewById(R.id.pwET)
        loginBTN = findViewById(R.id.loginBTN)
        registerTV = findViewById(R.id.registerTV)
        findTV = findViewById(R.id.findTV)

        registerTV.setOnClickListener {
            startActivity(Intent(this@LoginActivity, Register1Activity::class.java))
        }
        findTV.setOnClickListener {
            startActivity(Intent(this@LoginActivity, FindActivity::class.java))
        }

        loginBTN.setOnClickListener {
            val idText = idET.text.toString()
            val pwText = pwET.text.toString()
            if(idText.isNotEmpty() && pwText.isNotEmpty()) {
                val callPost = service.requestLogin(idText, pwText)
                callPost.enqueue(object: Callback<String?> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        if(response.isSuccessful) {
                            try {
                                val result = response.body()!!.toString()
                                if(result == "pass") {
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    ActivityCompat.finishAffinity(this@LoginActivity)
                                }
                                if(result == "pwFail") {
                                    Toast.makeText(this@LoginActivity,"비밀번호가 틀립니다", Toast.LENGTH_SHORT).show()
                                }
                                if(result == "idFail"){
                                    Toast.makeText(this@LoginActivity,"아이디가 틀립니다", Toast.LENGTH_SHORT).show()
                                }
                            }
                            catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                        else {
                            Toast.makeText(this@LoginActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            else {
                Toast.makeText(this@LoginActivity, "아이디 또는 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}