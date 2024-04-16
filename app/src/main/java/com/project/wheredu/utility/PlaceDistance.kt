package com.project.wheredu.utility

import com.google.maps.GeoApiContext
import com.google.maps.model.DistanceMatrix
import com.project.wheredu.BuildConfig

class PlaceDistance {
    companion object {
        private val geoApiContext = GeoApiContext.Builder().apiKey(BuildConfig.GOOGLEMAPAPI).build()
        fun calculateAndFormatDistance(latA: Double, longA: Double, latB: Double, longB: Double): String {
            val origins = "$latA,$longA"
            val destinations = "$latB,$longB"

            return try {
                val distanceMatrix: DistanceMatrix = com.google.maps.DistanceMatrixApi.newRequest(
                    geoApiContext
                )
                    .origins(origins)
                    .destinations(destinations)
                    .await()
                if (distanceMatrix.rows.isNotEmpty() && distanceMatrix.rows[0].elements.isNotEmpty()) {
                    val distance = distanceMatrix.rows[0].elements[0].distance.inMeters.toDouble()
                    String.format("%.2f", distance)+"m"
                } else {
                    "거리를 계산할 수 없습니다."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "오류가 발생했습니다."
            }
        }
    }
}