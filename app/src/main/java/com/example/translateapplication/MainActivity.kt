package com.example.translateapplication


import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message

import android.util.Log
import androidx.annotation.RequiresApi
import com.example.translateapplication.coroutine.onTextChange
import com.example.translateapplication.retrofit.RetrofitManage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.concurrent.thread
class MainActivity : AppCompatActivity() {
    var TAG = "Main"
    private var myJob: Job = Job()
    private var myCoroutineScope = CoroutineScope(Dispatchers.IO + myJob)
    private var myHandler: MyHandler? = null
    var source = "ko"
    var target = "en"
    var data: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myHandler = MyHandler()

        myCoroutineScope.launch {
            var flowText = transText.onTextChange()
            flowText
                .debounce(1000)
                .filter {
                    it?.length!! > 0
                }
                .onEach {
                        Log.d(TAG, "onCreate: 1")
                        data = it.toString()
                        RetrofitManage.instance.getTrans(source, target, data!!, myHandler!!)

                }
                .launchIn(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myCoroutineScope.cancel()
    }

    inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Log.d(TAG, "handleMessage:${msg.obj}")
            if(msg.obj != null) {
                when (msg.what) {
                    1 -> {
                        resultText.text = msg.obj.toString()
                    }

                }
            }
        }
    }
}
