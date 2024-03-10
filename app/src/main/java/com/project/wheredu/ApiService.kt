package com.project.wheredu

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("/app/login")
    fun requestLogin(
        @Field("userId") userId: String,
        @Field("userPw") userPw: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/contest")
    fun requestConnect(
        @Field("connectTest") conText: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/userInfo")
    fun userInfo(@Field("userID") userID: String): Call<String>

    @FormUrlEncoded
    @POST("/app/userInfo2")
    fun userInfo2(@Field("userID") userID: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/app/addTravelRecyclerList")
    fun addTravelRecyclerList(
        @Field("travelCnt") travelCnt:Int): Call<String>

    @FormUrlEncoded
    @POST("/app/returnTravelCnt")
    fun returnTravelCnt(
        @Field("returnCnt") returnCnt: Int): Call<Int>

    @FormUrlEncoded
    @POST("/app/forgetPw")
    fun forgetPw(@Field("userId") userId: String): Call<String>

    @FormUrlEncoded
    @POST("/app/addUser")
    fun addUser(
        @Field("addName") addName: String,
        @Field("addId") addId: String,
        @Field("addPw") addPw: String,
        @Field("addEmail") addEmail: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/changePw")
    fun changePw(
        @Field("userId") userId: String,
        @Field("changePw") changePw: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/changeData")
    fun changeData(
        @Field("ownNum") ownNum: String,
        @Field("changeType") changeType: Int,
        @Field("changeDate") changeDate: String,
        @Field("changeLoc") changeLoc: String,
        @Field("changePerson") changePerson: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/addTravelData")
    fun addTravelData(
        @Field("dateNum") dateNum: String,
        @Field("addType") addType: Int,
        @Field("addDate") addDate: String,
        @Field("addLoc") addLoc: String,
        @Field("addPerson") addPerson: String
    ): Call<String>

}