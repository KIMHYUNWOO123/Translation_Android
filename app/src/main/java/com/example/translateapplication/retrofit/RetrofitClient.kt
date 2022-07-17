package com.example.translateapplication.retrofit

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofitClient : Retrofit? = null

    fun getRetrofit(baseUrl : String):Retrofit?{
        if(retrofitClient == null){
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            Log.d("Gd", "getRetrofit: gd ")
        }
        return retrofitClient
    }
}