package com.project.wheredu.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.project.wheredu.MainActivity
import com.project.wheredu.R
import com.project.wheredu.utility.Service
import com.project.wheredu.utility.ToastMessage
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
    private lateinit var autoLoginCb: CheckBox

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val service = Service.getService()

    override fun onStart() {
        super.onStart()

        if(autoLoginCb.isChecked) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this@LoginActivity)
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        checkPermission()

        idET = findViewById(R.id.idET)
        pwET = findViewById(R.id.pwET)
        loginBTN = findViewById(R.id.loginBTN)
        registerTV = findViewById(R.id.registerTV)
        findTV = findViewById(R.id.findTV)
        autoLoginCb = findViewById(R.id.autoLoginCB)

        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)

        val storeID = preferences.getString("userID", "")
        if(storeID != "") {
            autoLoginCb.isChecked = true
        }

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
                                    editor = preferences.edit()
                                    editor.putString("accountID", idText)
                                    editor.putString("accountPW", pwText)
                                    editor.apply()
                                    if(autoLoginCb.isChecked) {
                                        editor.putString("userID", idText)
                                        editor.putString("userPW", pwText)
                                        editor.apply()
                                    }

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

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun checkPermission() {
        TedPermission.create()
            .setPermissionListener(object: PermissionListener {
                override fun onPermissionGranted() {}

                override fun onPermissionDenied(p0: MutableList<String>?) {
                    ToastMessage.show(this@LoginActivity, "권한을 허용해주세요")
                }
            })
            .setDeniedMessage("권한을 허용해주세요.\n[설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
            .setPermissions(Manifest.permission.POST_NOTIFICATIONS)
            .check()
    }
}