package com.example.translateapplication.retrofit

import android.os.Message
import android.util.Log
import com.example.translateapplication.MainActivity
import com.example.translateapplication.utils.Data
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RetrofitManage {
    val TAG = "RetrofitManage"
    companion object{
        var instance = RetrofitManage()
    }
    private var iRetrofit: IRetrofit? = RetrofitClient.getRetrofit(Data.BASEURL)?.create(IRetrofit::class.java)
    fun getTrans(source : String, target: String, text : String, myHandler : MainActivity.MyHandler ){

        var call = iRetrofit?.getTranslate(Data.clientId,Data.clientSecret,source, target, text)
        Log.d(TAG, "getTrans: $text")
        call?.enqueue(object : Callback<ReturnData>{
            override fun onResponse(call: Call<ReturnData>, response: Response<ReturnData>) {
                Log.d(TAG, "onResponse: ${response.body()!!.message.result.translatedText}")
                var message = Message.obtain()
                message.what = 1
                message.obj = response.body()!!.message.result.translatedText
                myHandler.sendMessage(message)
            }

            override fun onFailure(call: Call<ReturnData>, t: Throwable) {
                Log.d(TAG, "onFailure: $t ")
            }
        })
    }

}