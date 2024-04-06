package com.project.wheredu

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

class Distance {
    companion object {
        fun calculateAndFormatDistance(latA: Double, longA: Double, latB: Double, longB: Double): String {
            val locationA = LatLng(latA, longA)
            val locationB = LatLng(latB, longB)

            val distance: Double = SphericalUtil.computeDistanceBetween(locationA, locationB)

            val distanceInKilometers = distance / 1000
            val remainingMeters = distance % 1000

            val roundedDistanceInKilometers = distanceInKilometers.toInt()
            val roundedRemainingMeters = remainingMeters.toInt()

            return if (distance >= 1000) {
                "${roundedDistanceInKilometers}km ${roundedRemainingMeters}m 남았어요!"
            } else {
                "${roundedDistanceInKilometers * 1000 + roundedRemainingMeters}m 남았어요!"
            }
        }
    }
}