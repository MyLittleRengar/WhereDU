package com.project.wheredu.utility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.project.wheredu.R


class LocationService : Service() {
    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.lastLocation?.let { location ->
                val latitude = location.latitude
                val longitude = location.longitude
                onLocationUpdate(latitude, longitude)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    @SuppressLint("ForegroundServiceType")
    private fun startLocationService() {
        val channelId = "location_notification_channel"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        val resultIntent = Intent()
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(applicationContext, channelId)
        builder.setSmallIcon(R.drawable.ic_launcher_du_foreground)
        builder.setContentTitle("WhereDU")
        builder.setDefaults(NotificationCompat.DEFAULT_ALL)
        builder.setContentText("Running")
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(false)
        builder.setPriority(NotificationCompat.PRIORITY_MIN)
        if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
            val notificationChannel = NotificationChannel(channelId, "WhereDU", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "This channel is used by location service"
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.setInterval(30000)
        locationRequest.setFastestInterval(20000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper())
        startForeground(Constants.LOCATION_SERVICE_ID, builder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
    }

    private fun stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this)
            .removeLocationUpdates(mLocationCallback)
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            if (action != null) {
                if (action == Constants.ACTION_START_LOCATION_SERVICE) {
                    startLocationService()
                } else if (action == Constants.ACTION_STOP_LOCATION_SERVICE) {
                    stopLocationService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun onLocationUpdate(latitude: Double, longitude: Double) {
        val intent = Intent("LocationUpdate")
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}