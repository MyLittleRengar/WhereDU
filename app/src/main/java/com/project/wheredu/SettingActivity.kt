package com.project.wheredu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import com.project.wheredu.dialog.CustomUnregisterDialogAdapter
import com.project.wheredu.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class SettingActivity : AppCompatActivity() {

    private lateinit var settingBackIv: ImageView
    private lateinit var settingNotificationReceiveSw: SwitchMaterial
    private lateinit var settingLocationShareSw: SwitchMaterial
    private lateinit var settingLogoutTv: TextView
    private lateinit var settingUnregisterTv: TextView

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val service = Service.getService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        settingBackIv = findViewById(R.id.settingBackIV)
        settingNotificationReceiveSw = findViewById(R.id.settingNotificationReceiveSW)
        settingLocationShareSw = findViewById(R.id.settingLocationShareSW)
        settingLogoutTv = findViewById(R.id.settingLogoutTV)
        settingUnregisterTv = findViewById(R.id.settingUnregisterTV)

        settingNotificationReceiveSw.setOnCheckedChangeListener { _, onSwitch  ->
            preferences = getSharedPreferences("Permission", Context.MODE_PRIVATE)
            editor = preferences.edit()

            editor.putBoolean("NotificationReceive", onSwitch)
        }

        settingLocationShareSw.setOnCheckedChangeListener { _, onSwitch ->
            preferences = getSharedPreferences("Permission", Context.MODE_PRIVATE)
            editor = preferences.edit()

            editor.putBoolean("LocationShare", onSwitch)
        }

        settingBackIv.setOnClickListener {
            startActivity(Intent(this@SettingActivity, MyPageActivity::class.java))
            finish()
        }

        settingLogoutTv.setOnClickListener {
            preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
            editor = preferences.edit()
            editor.putString("userID", "")
            editor.putString("userPW", "")
            editor.apply()

            startActivity(Intent(this@SettingActivity, LoginActivity::class.java))
            ActivityCompat.finishAffinity(this@SettingActivity)
        }

        settingUnregisterTv.setOnClickListener {
            val dlg = CustomUnregisterDialogAdapter(this@SettingActivity)
            dlg.setOnAcceptClickedListener { content ->
                if(content == "pass") {
                    unregister()
                }
            }
            dlg.show()
        }
    }

    private fun unregister() {
        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        val storeNick = preferences.getString("accountNickname", "").toString()
        val callPost = service.unregister(storeNick)
        callPost.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result == "pass") {
                            Toast.makeText(this@SettingActivity, "회원탈퇴 되었습니다\n 이용해주셔서 감사합니다", Toast.LENGTH_SHORT).show()
                            editor = preferences.edit()
                            editor.remove("accountID")
                            editor.remove("accountNickname")
                            editor.remove("accountPW")
                            editor.remove("userPW")
                            editor.remove("userID")
                            editor.apply()
                            startActivity(Intent(this@SettingActivity, LoginActivity::class.java))
                            ActivityCompat.finishAffinity(this@SettingActivity)
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    Toast.makeText(this@SettingActivity, "오류가 발생했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(this@SettingActivity, "서버 연결에 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }
}