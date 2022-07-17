package com.example.translateapplication.retrofit

import android.util.Log
import com.example.translateapplication.utils.Data
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RetrofitManage {
    var data : String? = null
    val TAG = "RetrofitManage"
    companion object{
        var instance = RetrofitManage()
    }
    private var iRetrofit: IRetrofit? = RetrofitClient.getRetrofit(Data.BASEURL)?.create(IRetrofit::class.java)
    fun getTrans(source : String, target: String, text : String ) : String?{

        var call = iRetrofit?.getTranslate(Data.clientId,Data.clientSecret,source, target, text)
        Log.d(TAG, "getTrans: $text")
        call?.enqueue(object : Callback<ReturnData>{
            override fun onResponse(call: Call<ReturnData>, response: Response<ReturnData>) {
                Log.d(TAG, "onResponse: ${response.body()!!.message.result.translatedText}")
                data = response.body()!!.message.result.translatedText
            }

            override fun onFailure(call: Call<ReturnData>, t: Throwable) {
                Log.d(TAG, "onFailure: $t ")
            }
        })
        return data
    }

}