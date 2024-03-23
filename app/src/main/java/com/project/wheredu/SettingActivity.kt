package com.project.wheredu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import com.project.wheredu.login.LoginActivity

class SettingActivity : AppCompatActivity() {

    private lateinit var settingBackIv: ImageView
    private lateinit var settingNotificationReceiveSw: SwitchMaterial
    private lateinit var settingLocationShareSw: SwitchMaterial
    private lateinit var settingLogoutTv: TextView

    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        settingBackIv = findViewById(R.id.settingBackIV)
        settingNotificationReceiveSw = findViewById(R.id.settingNotificationReceiveSW)
        settingLocationShareSw = findViewById(R.id.settingLocationShareSW)
        settingLogoutTv = findViewById(R.id.settingLogoutTV)

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
    }
}