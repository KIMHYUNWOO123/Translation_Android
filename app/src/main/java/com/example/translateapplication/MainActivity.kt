package com.example.translateapplication


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message

import android.util.Log
import android.widget.Toast
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
    var source: String? = null
    var target: String? = null
    var data: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myHandler = MyHandler()
        transText.isEnabled = false

        thread(true) {
            while (true) {
                if (source != null && target != null && source != target) {
                    runOnUiThread {
                        transText.isEnabled = true
                    }
                    Log.d(TAG, "onCreate: 열림")
                }
                if(source == target){
                    runOnUiThread {
                        transText.isEnabled = false
                    }
                    Log.d(TAG, "onCreate: 닫힘")
                }
            }
        }
        language1.setOnCheckedChangeListener { radioGroup, i ->
            Log.d(TAG, "onCreate: 1")
            when (i) {
                R.id.ko1 -> source = "ko"
                R.id.en1 -> source = "en"
                R.id.ja1 -> source = "ja"
            }
        }
        language2.setOnCheckedChangeListener { radioGroup, i ->
            Log.d(TAG, "onCreate: 2")
            when (i) {
                R.id.ko2 -> target = "ko"
                R.id.en2 -> target = "en"
                R.id.ja2 -> target = "ja"
            }
        }
        myCoroutineScope.launch {
            Log.d(TAG, "onCreate: 호출전")
            var flowText = transText.onTextChange()
            flowText
                .debounce(1000)
                .filter {
                    it?.length!! > 0 && source != null && target != null && source != target
                }
                .onEach {
                    Log.d(TAG, "onCreate: 호출")
                    data = it.toString()
                    RetrofitManage.instance.getTrans(source!!, target!!, data!!, myHandler!!)
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
            Log.d("Handler", "handleMessage:${msg.obj}")
            if (msg.obj != null) {
                when (msg.what) {
                    1 -> {
                        resultText.text = msg.obj.toString()
                    }
                }
            }
        }
    }
}
