package com.project.wheredu.utility

import kotlin.math.*

class PlaceDistance {
    companion object {
        fun calculateAndFormatDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {
            val R = 6371 * 1000 // 지구의 반지름 (단위: 미터)
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val radLat1 = Math.toRadians(lat1)
            val radLat2 = Math.toRadians(lat2)

            val a = sin(dLat / 2) * sin(dLat / 2) +
                    sin(dLon / 2) * sin(dLon / 2) * cos(radLat1) * cos(radLat2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            val distance = R * c // 거리 (단위: 미터)
            return String.format("%.2f m", distance)
        }

        fun calculateAndFormatDistanceDouble(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val R = 6371 * 1000 // 지구의 반지름 (단위: 미터)
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val radLat1 = Math.toRadians(lat1)
            val radLat2 = Math.toRadians(lat2)

            val a = sin(dLat / 2) * sin(dLat / 2) +
                    sin(dLon / 2) * sin(dLon / 2) * cos(radLat1) * cos(radLat2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            return R * c
        }
    }
}