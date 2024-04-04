package com.project.wheredu

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

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

    private var myLocBoolean: Boolean = false

    private val accessFineLocation = 1000

    private lateinit var lm: LocationManager
    private var userNewLocation: Location? = null
    private var uNowPosition: MapPoint? = null

    private lateinit var bottomSheetPromiseInfoBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetFriendInfoBehavior: BottomSheetBehavior<LinearLayout>
    private val friendInfoBehavior by lazy { findViewById<LinearLayout>(R.id.per_bottom_sheetFriendInfo) }
    private val promiseInfoBehavior by lazy { findViewById<LinearLayout>(R.id.per_bottom_sheet_PromiseInfo) }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)

        fabMain = findViewById(R.id.fabMain)
        fabFriendInfo = findViewById(R.id.fabFriendInfo)
        fabPromiseInfo = findViewById(R.id.fabPromiseInfo)
        fabTouchDown = findViewById(R.id.fabTouchDown)

        fabMain2 = findViewById(R.id.fabMain2)
        fabPromiseLoc = findViewById(R.id.fabPromiseLoc)
        fabMyLoc = findViewById(R.id.fabMyLoc)

        initEvent()

        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        userNewLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        uNowPosition = MapPoint.mapPointWithGeoCoord(userNewLocation?.latitude!!, userNewLocation?.longitude!!)

        promiseMarker(35.9115006, 128.8160848)

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close)

        fabMain.setOnClickListener {
            toggleFab()
        }
        fabFriendInfo.setOnClickListener {
            bottomSheetFriendInfoBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetPromiseInfoBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            toggleFab()
        }
        fabPromiseInfo.setOnClickListener {
            bottomSheetPromiseInfoBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetFriendInfoBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            toggleFab()
        }
        fabTouchDown.setOnClickListener {
            toggleFab()
        }
        fabMain2.setOnClickListener {
            toggleFab2()
        }
        fabPromiseLoc.setOnClickListener {
            promiseLocation(35.9115006, 128.8160848)
            toggleFab2()
        }
        fabMyLoc.setOnClickListener {
            if(!myLocBoolean) {
                if (checkLocationService()) {
                    permissionCheck()
                } else {
                    Toast.makeText(this, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                stopTracking()
            }
            toggleFab2()
        }
    }

    private fun promiseMarker(uLatitude: Double, uLongitude: Double) {
        val marker = MapPOIItem()
        val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude,  uLongitude)
        marker.apply {
            itemName = "약속 장소"
            mapPoint = uNowPosition
            markerType = MapPOIItem.MarkerType.YellowPin
        }
        mapView.addPOIItem(marker)
    }

    private fun promiseLocation(uLatitude: Double, uLongitude: Double) {
        val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude,  uLongitude)
        mapView.setMapCenterPointAndZoomLevel(uNowPosition, 2, true)
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
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요")
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
                    builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요")
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
        val marker = MapPOIItem()
        marker.tag = 0
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading
        mapView.setZoomLevel(2, true)

        if(!myLocBoolean) {
            myLocBoolean = true
            fabMyLoc.setImageResource(R.drawable.colorpin)
        }
    }

    private fun stopTracking() {
        if(myLocBoolean) {
            myLocBoolean = false
            fabMyLoc.setImageResource(R.drawable.pin)
            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
        }
    }

    private fun initEvent() {
        persistentBottomSheetFriendInfoEvent()
        persistentBottomSheetPromiseInfoEvent()
        fabFriendInfo.setOnClickListener {
            bottomSheetFriendInfoBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetPromiseInfoBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        fabPromiseInfo.setOnClickListener {
            bottomSheetPromiseInfoBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetFriendInfoBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun persistentBottomSheetFriendInfoEvent() {
        bottomSheetFriendInfoBehavior = BottomSheetBehavior.from(friendInfoBehavior)
        bottomSheetFriendInfoBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {}
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(p0: View, newState: Int) {
                val tag = "friendInfoBehavior"
                when(newState) {
                    BottomSheetBehavior.STATE_COLLAPSED-> {
                        Log.d(tag, "onStateChanged: 접음")
                    }
                    BottomSheetBehavior.STATE_DRAGGING-> {
                        Log.d(tag, "onStateChanged: 드래그")
                    }
                    BottomSheetBehavior.STATE_EXPANDED-> {
                        Log.d(tag, "onStateChanged: 펼침")
                    }
                    BottomSheetBehavior.STATE_HIDDEN-> {
                        Log.d(tag, "onStateChanged: 숨기기")
                    }
                    BottomSheetBehavior.STATE_SETTLING-> {
                        Log.d(tag, "onStateChanged: 고정됨")
                    }
                }
            }


        })
    }

    private fun persistentBottomSheetPromiseInfoEvent() {
        bottomSheetPromiseInfoBehavior = BottomSheetBehavior.from(promiseInfoBehavior)
        bottomSheetPromiseInfoBehavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {}
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(p0: View, newState: Int) {
                val tag = "friendInfoBehavior"
                when(newState) {
                    BottomSheetBehavior.STATE_COLLAPSED-> {
                        Log.d(tag, "onStateChanged: 접음")
                    }
                    BottomSheetBehavior.STATE_DRAGGING-> {
                        Log.d(tag, "onStateChanged: 드래그")
                    }
                    BottomSheetBehavior.STATE_EXPANDED-> {
                        Log.d(tag, "onStateChanged: 펼침")
                    }
                    BottomSheetBehavior.STATE_HIDDEN-> {
                        Log.d(tag, "onStateChanged: 숨기기")
                    }
                    BottomSheetBehavior.STATE_SETTLING-> {
                        Log.d(tag, "onStateChanged: 고정됨")
                    }
                }
            }


        })
    }

}