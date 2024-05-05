package com.project.wheredu.recycler

import com.google.gson.annotations.SerializedName

data class ModelWeather(
    @SerializedName("rainType") var rainType: String = "",
    @SerializedName("humidity") var humidity: String = "",
    @SerializedName("sky") var sky: String = "",
    @SerializedName("temp") var temp: String = "",
    @SerializedName("fcstTime") var fcstTime: String = "",
)

data class WEATHER (val response : RESPONSE)
data class RESPONSE(val header : HEADER, val body : BODY)
data class HEADER(val resultCode : Int, val resultMsg : String)
data class BODY(val dataType : String, val items : ITEMS, val totalCount : Int)
data class ITEMS(val item : List<ITEM>)

data class ITEM(val category : String, val fcstDate : String, val fcstTime : String, val fcstValue : String)
