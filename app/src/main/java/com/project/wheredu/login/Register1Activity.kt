package com.project.wheredu.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.project.wheredu.R
import com.project.wheredu.api.Service
import retrofit2.*
import java.io.IOException

class Register1Activity : AppCompatActivity() {

    private lateinit var registerIDEt: EditText
    private lateinit var registerPWEt: EditText
    private lateinit var registerPWCheckEt: EditText
    private lateinit var registerNicknameCheckBtn: Button
    private lateinit var registerNextBtn: Button
    private lateinit var nicknameCheckedIv: ImageView

    private val service = Service.getService()

    private var idCheck = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register1)

        registerIDEt = findViewById(R.id.registerIdET)
        registerPWEt = findViewById(R.id.registerPasswordET)
        registerPWCheckEt = findViewById(R.id.registerPasswordCheckET)
        registerNicknameCheckBtn = findViewById(R.id.registerIdCheckedBTN)
        registerNextBtn = findViewById(R.id.registerNextBTN)
        nicknameCheckedIv = findViewById(R.id.idCheckedIV)

        registerNicknameCheckBtn.setOnClickListener {
            val idText = registerIDEt.text.toString()
            if(idText.isNotBlank()) {
                val callPost = service.idCheck(idText)
                callPost.enqueue(object: Callback<String?> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        if(response.isSuccessful) {
                            try {
                                val result = response.body()!!.toString()
                                if(result == "pass") {
                                    idCheck = true
                                    nicknameCheckedIv.visibility = View.VISIBLE
                                    registerIDEt.isEnabled = false
                                    registerNicknameCheckBtn.isEnabled = false
                                }
                                if(result == "idFail"){
                                    Toast.makeText(this@Register1Activity,"이미 중복된 아이디가 있습니다", Toast.LENGTH_SHORT).show()
                                }
                            }
                            catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                        else {
                            Toast.makeText(this@Register1Activity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        Toast.makeText(this@Register1Activity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            else {
                Toast.makeText(this@Register1Activity, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        registerNextBtn.setOnClickListener {
            val idText = registerIDEt.text.toString()
            val registerPwText = registerPWEt.text.toString()
            val registerPwCheckText = registerPWCheckEt.text.toString()
            if(idCheck) {
                if(registerPwText.isNotBlank()) {
                    if(registerPwCheckText.isNotBlank()) {
                        if(registerPwText == registerPwCheckText) {
                            val intent = Intent(this@Register1Activity, Register2Activity::class.java)
                            intent.putExtra("userID", idText)
                            intent.putExtra("userPW", registerPwText)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this@Register1Activity, "비밀번호가 서로 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        Toast.makeText(this@Register1Activity, "비밀번호 확인을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this@Register1Activity, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this@Register1Activity, "아이디 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}