package com.project.wheredu.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.project.wheredu.R
import com.project.wheredu.utility.Service
import com.project.wheredu.dialog.CustomFindIdDialogAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class FindActivity : AppCompatActivity() {

    private lateinit var findNicknameEt: EditText
    private lateinit var findBirthYearEt: EditText
    private lateinit var findBirthMonthEt: EditText
    private lateinit var findBirthDateEt: EditText
    private lateinit var findBirthBtn: Button
    private lateinit var findBackIv: ImageView

    private val service = Service.getService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)

        findNicknameEt = findViewById(R.id.findNicknameET)
        findBirthYearEt = findViewById(R.id.findBirthYearET)
        findBirthMonthEt = findViewById(R.id.findBirthMonthET)
        findBirthDateEt = findViewById(R.id.findBirthDateET)
        findBirthBtn = findViewById(R.id.findBirthBTN)
        findBackIv = findViewById(R.id.findBackIV)

        findBackIv.setOnClickListener {
            startActivity(Intent(this@FindActivity, LoginActivity::class.java))
            ActivityCompat.finishAffinity(this@FindActivity)
        }

        findBirthBtn.setOnClickListener {
            val nicknameText = findNicknameEt.text.toString()
            val birthYearText = findBirthYearEt.text.toString()
            val birthMonthText = findBirthMonthEt.text.toString()
            var birthDateText = findBirthDateEt.text.toString()

            if(findBirthDateEt.text.toString().length == 1) {
                birthDateText = "0"+findBirthDateEt.text.toString()
            }
            else if(findBirthDateEt.text.toString().length == 2) {
                birthDateText = findBirthDateEt.text.toString()
            }
            val resultBirth = "$birthYearText-$birthMonthText-$birthDateText"
            if(nicknameText.isNotBlank()) {
                if(birthYearText.isNotBlank() && birthMonthText.isNotBlank() && birthDateText.isNotBlank()) {
                    val callPost = service.findId(nicknameText, resultBirth)
                    callPost.enqueue(object: Callback<String?> {
                        override fun onResponse(call: Call<String?>, response: Response<String?>) {
                            if (response.isSuccessful) {
                                try {
                                    val result = response.body()!!.toString()
                                    val resultText = result.split("=")
                                    if (resultText[0] == "pass") {
                                        val dataBirth = resultText[1].split("/")
                                        val dlg = CustomFindIdDialogAdapter(this@FindActivity, dataBirth[0], this@FindActivity)
                                        dlg.show()
                                    }
                                    else if(result == "birthFail") {
                                        Toast.makeText(this@FindActivity, "생년월일이 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                    else if(result == "nickFail") {
                                        Toast.makeText(this@FindActivity, "닉네임이 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            } else {
                                Toast.makeText(this@FindActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<String?>, t: Throwable) {
                            Toast.makeText(this@FindActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                        }

                    })
                }
                else {
                    Toast.makeText(this@FindActivity, "생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this@FindActivity, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}