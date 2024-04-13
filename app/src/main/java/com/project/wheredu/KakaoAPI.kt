package com.project.wheredu

import com.project.wheredu.recycler.ResultSearchKeyword
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoAPI {
    @GET("v2/local/search/keyword.json")
    fun getSearchKeyword(
        @Header("Authorization") key: String,
        @Query("query") query: String
    ): Call<ResultSearchKeyword>

    @GET("v2/local/search/category.json")
    fun getSearchCategory(
        @Header("Authorization") key: String,
        @Query("category_group_code") category: String,
        @Query("y") y: String,
        @Query("x") x: String,
        @Query("radius") radius: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<ResultSearchKeyword>
}