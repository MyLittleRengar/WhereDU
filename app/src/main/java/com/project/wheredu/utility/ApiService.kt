package com.project.wheredu.utility

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

    @FormUrlEncoded
    @POST("/app/friendAdd")
    fun friendAdd(
        @Field("userNickname") userNickname: String,
        @Field("friendNickname") friendNickname: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/returnFriendCount")
    fun returnFriendCount(
        @Field("userNickname") userNickname: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/returnBookMarkFriendCount")
    fun returnBookMarkFriendCount(
        @Field("userNickname") userNickname: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/bookmarkFriendListData")
    fun bookmarkFriendListData(
        @Field("userNickname") userNickname: String,
        @Field("friendInt") friendInt: Int
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/friendListData")
    fun friendListData(
        @Field("userNickname") userNickname: String,
        @Field("friendInt") friendInt: Int
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/returnPromiseFriendCount")
    fun returnPromiseFriendCount(
        @Field("userNickname") userNickname: String
    ): Call<String>
    @FormUrlEncoded
    @POST("/app/promiseFriendListData")
    fun promiseFriendListData(
        @Field("userNickname") userNickname: String,
        @Field("friendInt") friendInt: Int
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/searchText")
    fun searchText(
        @Field("searchText") searchText: String,
        @Field("userNickname") userNickname: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/friendDelete")
    fun friendDelete(
        @Field("friendNickname") friendNickname: String,
        @Field("userNickname") userNickname: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/friendBookMarkChange")
    fun friendBookMarkChange(
        @Field("check") check: Boolean,
        @Field("friendNickname") friendNickname: String,
        @Field("userNickname") userNickname: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/unregister")
    fun unregister(
        @Field("userNickname") userNickname: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/addPromise")
    fun addPromise(
        @Field("promiseOwner") promiseOwner: String,
        @Field("promiseName") promiseName: String,
        @Field("promiseLatitude") promiseLatitude: Double,
        @Field("promiseLongitude") promiseLongitude: Double,
        @Field("promisePlace") promisePlace: String,
        @Field("promisePlaceDetail") promisePlaceDetail: String,
        @Field("promiseDate") promiseDate: String,
        @Field("promiseTime") promiseTime: String,
        @Field("promiseMember") promiseMember: List<String>,
        @Field("promiseMemo") promiseMemo: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/returnPromiseData")
    fun returnPromiseData(
        @Field("promiseName") promiseName: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/calendarPromiseData")
    fun calendarPromiseData(
        @Field("promiseName") promiseName: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/selectPromiseData")
    fun selectPromiseData(
        @Field("promiseDate") promiseDate: String,
        @Field("cnt") cnt: Int
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/returnSelectPromiseData")
    fun returnSelectPromiseData(
        @Field("promiseDate") promiseDate: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/deletePromise")
    fun deletePromise(
        @Field("name") name: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/eventData")
    fun eventData(
        @Field("event") event: Int
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/returnEventData")
    fun returnEventData(
        @Field("event") event: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/eventInfoData")
    fun eventInfoData(
        @Field("eventTitle") eventTitle: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/noticeData")
    fun noticeData(
        @Field("notice") notice: Int
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/returnNoticeData")
    fun returnNoticeData(
        @Field("notice") notice: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/noticeInfoData")
    fun noticeInfoData(
        @Field("noticeTitle") noticeTitle: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/inquiry")
    fun inquiry(
        @Field("nickname") nickname: String,
        @Field("date") date: String,
        @Field("content") content: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/recentPromise")
    fun recentPromise(
        @Field("userName") userName: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/pastPromise")
    fun pastPromise(
        @Field("userName") userName: String,
        @Field("cnt") cnt: Int
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/returnPastPromise")
    fun returnPastPromise(
        @Field("userName") userName: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/promiseTouchdown")
    fun promiseTouchdown(
        @Field("promiseName") promiseName: String,
        //@Field("")
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/location")
    fun location(
        @Field("nickname") nickname: String,
        @Field("longitude") longitude: Double,
        @Field("latitude") latitude: Double
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/memberLocation")
    fun memberLocation(
        @Field("nickname") nickname: String
    ): Call<String>

    @FormUrlEncoded
    @POST("/app/memberTouchdown")
    fun memberTouchdown(
        @Field("nickname") nickname: String
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
