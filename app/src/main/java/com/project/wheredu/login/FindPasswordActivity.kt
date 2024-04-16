package com.project.wheredu.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.project.wheredu.dialog.CustomFindPasswordDialogAdapter
import com.project.wheredu.R
import com.project.wheredu.utility.Service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FindPasswordActivity : AppCompatActivity() {

    private lateinit var findPwIdEt: EditText
    private lateinit var findBirthDp: DatePicker
    private lateinit var findBirthBtn: Button
    private lateinit var findBack2Iv: ImageView

    private val service = Service.getService()

    private var inputBirth = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password)

        findPwIdEt = findViewById(R.id.findPwIdET)
        findBirthDp = findViewById(R.id.findBirthDP)
        findBirthBtn = findViewById(R.id.findBirthBTN)
        findBack2Iv = findViewById(R.id.findBack2IV)

        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val maxLocalDate = LocalDate.parse("2030/12/31", formatter)
        val minLocalDate = LocalDate.parse("2000/01/01", formatter)

        findBirthDp.maxDate = maxLocalDate.toEpochDay() * 24 * 60 * 60 * 1000
        findBirthDp.minDate = minLocalDate.toEpochDay() * 24 * 60 * 60 * 1000

        findBack2Iv.setOnClickListener {
            startActivity(Intent(this@FindPasswordActivity, LoginActivity::class.java))
            ActivityCompat.finishAffinity(this@FindPasswordActivity)
        }

        findBirthBtn.setOnClickListener {
            val findPwIdText = findPwIdEt.text.toString()
            if(findPwIdText.isNotBlank()) {
                if(inputBirth != "") {
                    val callPost = service.forgetPw(findPwIdText, inputBirth)
                    callPost.enqueue(object: Callback<String?> {
                        override fun onResponse(call: Call<String?>, response: Response<String?>) {
                            if(response.isSuccessful) {
                                try {
                                    val result = response.body()!!.toString()
                                    if(result == "pass") {
                                        val dlg = CustomFindPasswordDialogAdapter(this@FindPasswordActivity)
                                        dlg.setOnAcceptClickedListener { content ->
                                            sendData(content)
                                        }
                                        dlg.show()
                                    }
                                    if(result == "birthFail"){
                                        Toast.makeText(this@FindPasswordActivity,"생년월일이 틀립니다", Toast.LENGTH_SHORT).show()
                                    }
                                    if(result == "idFail") {
                                        Toast.makeText(this@FindPasswordActivity,"아이디가 틀립니다", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                            else {
                                Toast.makeText(this@FindPasswordActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<String?>, t: Throwable) {
                            Toast.makeText(this@FindPasswordActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                else {
                    Toast.makeText(this@FindPasswordActivity, "생년월일을 선택해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this@FindPasswordActivity, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        findBirthDp.setOnDateChangedListener { _, y, m, d ->
            var date = ""
            if(d.toString().length == 1) {
                date = "0$d"
            }
            else if(d.toString().length == 2) {
                date = "$d"
            }
            inputBirth = "$y-${m+1}-$date"
        }
    }

    private fun sendData(password: String) {
        val inputText = findPwIdEt.text.toString()
        val callPost = service.changePw(inputText, password)
        callPost.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result == "pass") {
                            Toast.makeText(this@FindPasswordActivity, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@FindPasswordActivity, LoginActivity::class.java))
                            ActivityCompat.finishAffinity(this@FindPasswordActivity)
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@FindPasswordActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@FindPasswordActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

}