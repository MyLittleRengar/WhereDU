package com.project.wheredu.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.project.wheredu.R
import com.project.wheredu.utility.Service
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Register2Activity : AppCompatActivity() {

    private lateinit var registerNicknameEt: EditText
    private lateinit var nicknameCheckedIv: ImageView
    private lateinit var registerNicknameCheckedBtn: Button
    private lateinit var registerGenderRg: RadioGroup
    private lateinit var registerBirthDp: DatePicker
    private lateinit var registerDoneBtn: Button

    private var nickCheck = false
    private val service = Service.getService()
    private var userGender = ""
    private var userBirth = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register2)
        val userID = intent.getStringExtra("userID").toString()
        val userPW = intent.getStringExtra("userPW").toString()
        val userEmail = intent.getStringExtra("userEmail").toString()

        registerNicknameEt = findViewById(R.id.registerNicknameET)
        nicknameCheckedIv = findViewById(R.id.nicknameCheckedIV)
        registerNicknameCheckedBtn = findViewById(R.id.registerNicknameCheckedBTN)
        registerGenderRg = findViewById(R.id.registerGenderRG)
        registerBirthDp = findViewById(R.id.registerBirthDP)
        registerDoneBtn = findViewById(R.id.registerDoneBTN)

        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val maxLocalDate = LocalDate.parse("2030/12/31", formatter)
        val minLocalDate = LocalDate.parse("2000/01/01", formatter)

        registerBirthDp.maxDate = maxLocalDate.toEpochDay() * 24 * 60 * 60 * 1000
        registerBirthDp.minDate = minLocalDate.toEpochDay() * 24 * 60 * 60 * 1000

        registerDoneBtn.setOnClickListener {
            val userNickname = registerNicknameEt.text.toString()
            if (nickCheck) {
                if (userGender != "") {
                    if (userBirth != "") {
                        val callPost = service.addUser(userID, userPW, userEmail, userNickname, userGender, userBirth)
                        callPost.enqueue(object : Callback<String?> {
                            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                                if (response.isSuccessful) {
                                    try {
                                        val result = response.body()!!.toString()
                                        if (result == "pass") {
                                            uploadImage(userNickname)
                                            Toast.makeText(this@Register2Activity, userNickname+"님 회원가입을 축하합니다.", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this@Register2Activity, LoginActivity::class.java))
                                            ActivityCompat.finishAffinity(this@Register2Activity)
                                        }
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                } else {
                                    Toast.makeText(this@Register2Activity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<String?>, t: Throwable) {
                                Toast.makeText(this@Register2Activity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                    else {
                        Toast.makeText(this@Register2Activity, "생년월일을  선택 해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(this@Register2Activity, "성별을 선택 해주세요", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this@Register2Activity, "닉네임 중복확인을 해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        registerBirthDp.setOnDateChangedListener { _, y, m, d ->
            userBirth = "$y-$m-$d"
        }

        registerNicknameCheckedBtn.setOnClickListener {
            val nicknameText = registerNicknameEt.text.toString()
            if (nicknameText.isNotBlank()) {
                val callPost = service.nicknameCheck(nicknameText)
                callPost.enqueue(object : Callback<String?> {
                    override fun onResponse(call: Call<String?>, response: Response<String?>) {
                        if (response.isSuccessful) {
                            try {
                                val result = response.body()!!.toString()
                                if (result == "pass") {
                                    nickCheck = true
                                    nicknameCheckedIv.visibility = View.VISIBLE
                                    registerNicknameEt.isEnabled = false
                                    registerNicknameCheckedBtn.isEnabled = false
                                }
                                if (result == "NickFail") {
                                    Toast.makeText(this@Register2Activity, "이미 중복된 닉네임이 있습니다", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        } else {
                            Toast.makeText(this@Register2Activity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<String?>, t: Throwable) {
                        Toast.makeText(this@Register2Activity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this@Register2Activity, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        registerGenderRg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.registerBoyRB -> userGender = "1"
                R.id.registerGirlRB -> userGender = "0"
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun uploadImage(userNickname: String) {
        val imageUri = Uri.parse("android.resource://$packageName/${R.drawable.free}")
        val file = imageUri.path?.let { File(it) }
        val requestFile =
            file?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body = requestFile?.let { MultipartBody.Part.createFormData("image", file.name, it) }

        val usernameRequestBody = userNickname.toRequestBody("text/plain".toMediaTypeOrNull())

        GlobalScope.launch(Dispatchers.IO) {
            body?.let { service.uploadImage(usernameRequestBody, it) }
                ?.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {}

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("MyPageActivity", "Image upload failed: ${t.message}")
                    }
                })
        }
    }
}