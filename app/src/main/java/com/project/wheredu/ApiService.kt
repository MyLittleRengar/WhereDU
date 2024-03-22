package com.project.wheredu

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("/app/upload")
    fun uploadImage(
        @Part("userNick") userNick: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<Void>

    @GET("/app/downloadImage")
    fun downloadImage(
        @Query("userNick") userNick: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("/app/login")
    fun requestLogin(
        @Field("userId") userId: String,
        @Field("userPw") userPw: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/idCheck")
    fun idCheck(
        @Field("userId") userId: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/nicknameCheck")
    fun nicknameCheck(
        @Field("userNickname") userNickname: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/forgetPw")
    fun forgetPw(
        @Field("inputId") inputId: String,
        @Field("inputBirth") inputBirth: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/addUser")
    fun addUser(
        @Field("addId") addId: String,
        @Field("addPw") addPw: String,
        @Field("addNickname") addNickname: String,
        @Field("addGender") addGender: String,
        @Field("addBirth") addBirth: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/findId")
    fun findId(
        @Field("findNickname") findNickname: String,
        @Field("findBirth") findBirth: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/changePw")
    fun changePw(
        @Field("userId") userId: String,
        @Field("changePw") changePw: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/getUserData")
    fun getUserData(
        @Field("userId") userId: String
    ): Call<String>

    /*@FormUrlEncoded
    @POST("/app/changeData")
    fun changeData(
        @Field("ownNum") ownNum: String,
        @Field("changeType") changeType: Int,
        @Field("changeDate") changeDate: String,
        @Field("changeLoc") changeLoc: String,
        @Field("changePerson") changePerson: String
    ): Call<String>*/

    /*@FormUrlEncoded
    @POST("/app/addTravelRecyclerList")
    fun addTravelRecyclerList(
        @Field("travelCnt") travelCnt:Int): Call<String>*/
}
