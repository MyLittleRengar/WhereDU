package com.project.wheredu

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.daum.mf.map.api.MapView

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var currentLocBtn: Button
    private lateinit var currentLosBtn: Button

    private lateinit var fabMain: FloatingActionButton
    private lateinit var fabPromiseInfo: FloatingActionButton
    private lateinit var fabFriendInfo: FloatingActionButton
    private lateinit var fabTouchDown: FloatingActionButton

    private lateinit var fabMain2: FloatingActionButton
    private lateinit var fabPromiseLoc: FloatingActionButton
    private lateinit var fabMyLoc: FloatingActionButton

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private var isFabOpen: Boolean = false
    private var isFabOpen2: Boolean = false

    private val accessFineLocation = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)
        currentLocBtn = findViewById(R.id.currentLocBTN)
        currentLosBtn = findViewById(R.id.currentLosBTN)

        fabMain = findViewById(R.id.fabMain)
        fabFriendInfo = findViewById(R.id.fabFriendInfo)
        fabPromiseInfo = findViewById(R.id.fabPromiseInfo)
        fabTouchDown = findViewById(R.id.fabTouchDown)

        fabMain2 = findViewById(R.id.fabMain2)
        fabPromiseLoc = findViewById(R.id.fabPromiseLoc)
        fabMyLoc = findViewById(R.id.fabMyLoc)

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close)

        fabMain.setOnClickListener {
            toggleFab()
        }
        fabFriendInfo.setOnClickListener {
            toggleFab()
        }
        fabPromiseInfo.setOnClickListener {
            toggleFab()
        }
        fabTouchDown.setOnClickListener {
            toggleFab()
        }
        fabMain2.setOnClickListener {
            toggleFab2()
        }
        fabPromiseLoc.setOnClickListener {
            toggleFab2()
        }
        fabMyLoc.setOnClickListener {
            toggleFab2()
        }

        currentLocBtn.setOnClickListener {
            if (checkLocationService()) {
                permissionCheck()
            } else {
                Toast.makeText(this, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
            }
        }

        currentLosBtn.setOnClickListener {
            stopTracking()
        }
    }

    private fun toggleFab() {
        if(isFabOpen) {
            fabMain.setImageResource(R.drawable.add)
            fabFriendInfo.startAnimation(fabClose)
            fabPromiseInfo.startAnimation(fabClose)
            fabTouchDown.startAnimation(fabClose)
            fabFriendInfo.isClickable = false
            fabPromiseInfo.isClickable = false
            fabTouchDown.isClickable = false
            isFabOpen = false
        }
        else {
            fabMain.setImageResource(R.drawable.x_icon)
            fabFriendInfo.startAnimation(fabOpen)
            fabPromiseInfo.startAnimation(fabOpen)
            fabTouchDown.startAnimation(fabOpen)
            fabFriendInfo.isClickable = true
            fabPromiseInfo.isClickable = true
            fabTouchDown.isClickable = true
            isFabOpen = true
        }
    }

    private fun toggleFab2() {
        if(isFabOpen2) {
            fabMain2.setImageResource(R.drawable.navigation)
            fabPromiseLoc.startAnimation(fabClose)
            fabMyLoc.startAnimation(fabClose)
            fabPromiseLoc.isClickable = false
            fabMyLoc.isClickable = false
            isFabOpen2 = false
        }
        else {
            fabMain2.setImageResource(R.drawable.empty_navigation)
            fabPromiseLoc.startAnimation(fabOpen)
            fabMyLoc.startAnimation(fabOpen)
            fabPromiseLoc.isClickable = true
            fabMyLoc.isClickable = true
            isFabOpen2 = true
        }
    }

    private fun permissionCheck() {
        val preference = getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 권한 거절 (다시 한 번 물어봄)
                val builder = AlertDialog.Builder(this)
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { _, _ ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), accessFineLocation)
                }
                builder.setNegativeButton("취소") { _, _ ->

                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    // 최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), accessFineLocation)
                } else {
                    // 다시 묻지 않음 클릭 (앱 정보 화면으로 이동)
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                        startActivity(intent)
                    }
                    builder.setNegativeButton("취소") { _, _ ->

                    }
                    builder.show()
                }
            }
        } else {
            // 권한이 있는 상태
            startTracking()
        }
    }

    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun startTracking() {
        mapView.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading
        //TrackingModeOnWithoutHeading

        //val compass = Kakao
    }

    private fun stopTracking() {
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }
}