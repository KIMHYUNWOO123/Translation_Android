package com.example.translateapplication.retrofit

import retrofit2.Call
import retrofit2.http.*

interface IRetrofit {
    @POST("/v1/papago/n2mt")
    @FormUrlEncoded
    fun getTranslate(
        @Header("X-Naver-Client-Id") clientId : String,
        @Header("X-Naver-Client-Secret") clientSecret : String,
        @Field("source") source : String,
        @Field("target") target : String,
        @Field("text") text : String
    ) : Call<ReturnData>
}

