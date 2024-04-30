package com.project.wheredu

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.wheredu.recycler.MapMemberItem
import com.project.wheredu.recycler.MapMemberListAdapter
import com.project.wheredu.utility.Constants
import com.project.wheredu.utility.GetAddress
import com.project.wheredu.utility.LocationService
import com.project.wheredu.utility.PlaceDistance
import com.project.wheredu.utility.Service
import com.project.wheredu.utility.ToastMessage
import net.daum.mf.map.api.MapCircle
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


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

    private lateinit var promiseName: String
    private lateinit var promiseDate: String
    private lateinit var promiseTime: String
    private lateinit var promisePlace: String
    private lateinit var promisePlaceDetail: String
    private lateinit var promiseMember: String
    private lateinit var promiseMemo: String

    private lateinit var preferences: SharedPreferences
    private lateinit var storeNick: String

    private val service = Service.getService()
    private var listItems = arrayListOf<MapMemberItem>()
    private var mapMemberListAdapter = MapMemberListAdapter(listItems)

    private lateinit var bottomSheetPromiseInfoBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetFriendInfoBehavior: BottomSheetBehavior<LinearLayout>
    private val friendInfoBehavior by lazy { findViewById<LinearLayout>(R.id.per_bottom_sheetFriendInfo) }
    private val promiseInfoBehavior by lazy { findViewById<LinearLayout>(R.id.per_bottom_sheet_PromiseInfo) }

    private lateinit var buttonStartLocationUpdates: Button
    private lateinit var buttonStopLocationUpdates: Button
    private val requestCodeLocationPermission = 1

    private var marker = MapPOIItem()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        registerLocationUpdateReceiver()

        preferences = getSharedPreferences("Account", Context.MODE_PRIVATE)
        storeNick = preferences.getString("accountNickname", "").toString()

        val getIntent = intent
        val promiseName = getIntent.getStringExtra("promiseName").toString()
        val promiseLatitude = getIntent.getDoubleExtra("promiseLatitude", 0.0)
        val promiseLongitude = getIntent.getDoubleExtra("promiseLongitude", 0.0)

        mapView = findViewById(R.id.mapView)

        fabMain = findViewById(R.id.fabMain)
        fabFriendInfo = findViewById(R.id.fabFriendInfo)
        fabPromiseInfo = findViewById(R.id.fabPromiseInfo)
        fabTouchDown = findViewById(R.id.fabTouchDown)

        fabMain2 = findViewById(R.id.fabMain2)
        fabPromiseLoc = findViewById(R.id.fabPromiseLoc)
        fabMyLoc = findViewById(R.id.fabMyLoc)

        buttonStartLocationUpdates = findViewById(R.id.buttonStartLocationUpdates)
        buttonStopLocationUpdates = findViewById(R.id.buttonStopLocationUpdates)

        buttonStartLocationUpdates.setOnClickListener {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCodeLocationPermission)
            } else {
                startLocationService()
            }
        }
        buttonStopLocationUpdates.setOnClickListener {
            stopLocationService()
        }

        returnPromiseData(promiseName)

        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        userNewLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        uNowPosition = MapPoint.mapPointWithGeoCoord(userNewLocation?.latitude!!, userNewLocation?.longitude!!)

        promiseMarker(promiseLongitude, promiseLatitude)

        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(userNewLocation?.latitude!!,  userNewLocation?.longitude!!), 2, true)

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
            userLocationDistance(promiseName, userNewLocation?.latitude!!, userNewLocation?.longitude!!, 35.9115006, 128.8160848)
            toggleFab()
        }
        fabMain2.setOnClickListener {
            toggleFab2()
        }
        fabPromiseLoc.setOnClickListener {
            promiseLocation(promiseLatitude, promiseLongitude)
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

    private fun returnPromiseData(name: String) {
        val callPost = service.returnPromiseData(name)
        callPost.enqueue(object: Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        replaceData(result)
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    ToastMessage.show(this@MapActivity, "오류가 발생했습니다")
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                ToastMessage.show(this@MapActivity, "서버 연결에 오류가 발생했습니다")
            }
        })
    }
    private fun replaceData(result: String) {
        val jsonArray = JSONArray(result)
        val jsonObject = jsonArray.getJSONObject(0)

        val promiseNameResult = jsonObject.getString("promiseName")
        val promisePlaceResult = jsonObject.getString("promisePlace")
        val promisePlaceDetailResult = jsonObject.getString("promisePlaceDetail")
        val promiseDateResult = jsonObject.getString("promiseDate")
        val promiseTimeResult = jsonObject.getString("promiseTime")
        val promiseMembersResult = jsonObject.getString("promiseMember")
        val promiseMemoResult = jsonObject.getString("promiseMemo")

        val membersList = promiseMembersResult.split(", ")

        val textSplit = listOf(promiseNameResult, promisePlaceResult, promisePlaceDetailResult, promiseDateResult, promiseTimeResult, membersList.joinToString(", "), promiseMemoResult)
        promiseName = textSplit[0]
        promisePlace = textSplit[1]
        promisePlaceDetail = textSplit[2]
        promiseDate = textSplit[3]
        promiseTime = textSplit[4]
        promiseMember = textSplit[5]
        promiseMemo = textSplit[6]
        initEvent()
    }

    private fun userLocationDistance(promiseName: String, uLatitude: Double, uLongitude: Double, myLatitude: Double, myLongitude: Double) {
        val distance = PlaceDistance.calculateAndFormatDistanceDouble(uLatitude, uLongitude, myLatitude, myLongitude)
        if(distance <= 200) {
            checkTouchdown(promiseName)
        }
        else {
            ToastMessage.show(this@MapActivity, "아직 터치다운을 할 수 없습니다")
        }
    }

    private fun checkTouchdown(name: String) {
        val callPost = service.promiseTouchdown(name)
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result == "pass") {
                            ToastMessage.show(this@MapActivity, "터치 다운!!")
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    ToastMessage.show(this@MapActivity, "오류가 발생했습니다")
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                ToastMessage.show(this@MapActivity, "서버 연결에 오류가 발생했습니다")
            }
        })
    }

    private fun promiseMarker(uLatitude: Double, uLongitude: Double) {
        val circle = MapCircle(MapPoint.mapPointWithGeoCoord(uLatitude, uLongitude), 200, Color.argb(128, 255, 0, 0), Color.argb(128, 255, 0 ,0))
        val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude,  uLongitude)
        marker.apply {
            itemName = "약속 장소"
            mapPoint = uNowPosition
            markerType = MapPOIItem.MarkerType.YellowPin
        }
        mapView.addCircle(circle)
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
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
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), accessFineLocation)
                } else {
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

    private fun View.setTextViewText(id: Int, text: String) {
        findViewById<TextView>(id).text = text
    }

    private fun initEvent() {
        persistentBottomSheetFriendInfoEvent()
        persistentBottomSheetPromiseInfoEvent()

        fabFriendInfo.setOnClickListener {
            with(findViewById<View>(R.id.per_bottom_sheetFriendInfo)) {
                val rvId = findViewById<RecyclerView>(R.id.map_done_memberRV)
                rvId.layoutManager = LinearLayoutManager(this@MapActivity, LinearLayoutManager.VERTICAL, false)
                rvId.adapter = mapMemberListAdapter
                memberLocation()
            }
            bottomSheetFriendInfoBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetPromiseInfoBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        fabPromiseInfo.setOnClickListener {
            with(findViewById<View>(R.id.per_bottom_sheet_PromiseInfo)) {
                setTextViewText(R.id.bottomSheet_promiseNameTV, promiseName)
                setTextViewText(R.id.bottomSheet_PromiseDateTV, "$promiseDate, $promiseTime")
                setTextViewText(R.id.bottomSheet_PromisePlaceTV, promisePlace)
                setTextViewText(R.id.bottomSheet_PromiseDetailTV, promisePlaceDetail)
                setTextViewText(R.id.bottomSheet_PromiseMemberTV, promiseMember)
                setTextViewText(R.id.bottomSheet_PromiseMemoTV, promiseMemo)
            }
            bottomSheetPromiseInfoBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetFriendInfoBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun persistentBottomSheetFriendInfoEvent() {
        bottomSheetFriendInfoBehavior = BottomSheetBehavior.from(friendInfoBehavior)
    }

    private fun persistentBottomSheetPromiseInfoEvent() {
        bottomSheetPromiseInfoBehavior = BottomSheetBehavior.from(promiseInfoBehavior)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeLocationPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService()
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isLocationServiceRunning(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (LocationService::class.java.name == service.service.className) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }

    private fun startLocationService() {
        if (!isLocationServiceRunning()) {
            val intent = Intent(applicationContext, LocationService::class.java)
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE)
            startService(intent)
        }
    }

    private fun stopLocationService() {
        if (isLocationServiceRunning()) {
            val intent = Intent(applicationContext, LocationService::class.java)
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE)
            startService(intent)
        }
    }

    private val locationUpdateReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent?.action == "LocationUpdate") {
                val latitude = intent.getDoubleExtra("latitude", 0.0)
                val longitude = intent.getDoubleExtra("longitude", 0.0)
                handleLocationUpdate(latitude,longitude)
                val message = GetAddress.getAddress(this@MapActivity, latitude, longitude)
                Log.v("LOCATION_UPDATE_ADDRESS", message)
            }
        }
    }

    private fun handleLocationUpdate(latitude: Double, longitude: Double) {
        val callPost = service.location(storeNick, longitude, latitude)
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if(response.isSuccessful) {
                    try {
                        val result = response.body()!!.toString()
                        if(result == "pass") {
                            memberLocation()
                        }
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                else {
                    ToastMessage.show(this@MapActivity, "오류가 발생했습니다")
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                ToastMessage.show(this@MapActivity, "서버 연결에 오류가 발생했습니다")
            }
        })
    }

    private fun memberLocation() {
        val memberList = promiseMember.split(", ")
        for(i in memberList.indices) {
            val callPost = service.memberLocation(memberList[i])
            callPost.enqueue(object: Callback<String> {
                override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    if(response.isSuccessful) {
                        try {
                            val result = response.body()!!.toString()
                            memberTouchdownCheck(result, memberList[i], i)
                        }
                        catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    else {
                        ToastMessage.show(this@MapActivity, "오류가 발생했습니다")
                    }
                }
                override fun onFailure(call: Call<String?>, t: Throwable) {
                    ToastMessage.show(this@MapActivity, "서버 연결에 오류가 발생했습니다")
                }
            })
        }
    }

    private fun memberTouchdownCheck(result: String, nickname: String, cnt: Int) {
        val callPost = service.memberTouchdown(nickname)
        callPost.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful) {
                    try {
                        val nowResult = response.body()!!.toString()
                        selectReplaceData(result, cnt)
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                ToastMessage.show(this@MapActivity, "서버 연결에 오류가 발생했습니다")
            }
        })
    }

    private fun selectReplaceData(result: String, cnt: Int) {
        val jsonObject = JSONObject(result)

        val nickname = jsonObject.getString("nickname")
        val longitude = jsonObject.getString("longitude")
        val latitude = jsonObject.getString("latitude")

        val textSplit = listOf(nickname, longitude, latitude)
        initRecycle(textSplit, cnt)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycle(data: List<String>, cnt: Int) {
        if(data.isNotEmpty()) {
            listItems.clear()
            val mapCharacterDrawables = arrayOf(R.drawable.mapcharacter0, R.drawable.mapcharacter1, R.drawable.mapcharacter2, R.drawable.mapcharacter3, R.drawable.mapcharacter4)
            val item = MapMemberItem(nickname = data[0], img = mapCharacterDrawables[cnt])
            listItems.add(item)
            mapMemberListAdapter.notifyDataSetChanged()
        }
    }

    private fun registerLocationUpdateReceiver() {
        val filter = IntentFilter("LocationUpdate")
        LocalBroadcastManager.getInstance(this).registerReceiver(locationUpdateReceiver, filter)
    }

    private fun unregisterLocationUpdateReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationUpdateReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterLocationUpdateReceiver()
    }
}