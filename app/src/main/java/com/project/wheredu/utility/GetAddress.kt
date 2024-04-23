package com.project.wheredu.utility

import android.content.Context
import android.location.Geocoder
import android.util.Log
import java.io.IOException
import java.util.Locale

class GetAddress {
    companion object {
        fun getAddress(context: Context, latitude: Double, longitude: Double): String {
            val geocoder = Geocoder(context, Locale.KOREA)
            var nowAddress = ""
            try {
                val addressList = geocoder.getFromLocation(latitude, longitude, 1)
                if (addressList != null) {
                    if (addressList.isNotEmpty()) {
                        nowAddress = addressList[0].getAddressLine(0)
                    }
                }
            } catch (e: IOException) {
                Log.e("LOCATION_UPDATE_ADDRESS", "주소 가져오기 오류", e)
                nowAddress = "오류가 발생했습니다"
            }
            return nowAddress
        }
    }
}