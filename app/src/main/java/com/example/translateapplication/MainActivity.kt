package com.example.translateapplication


import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

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
    var source = "ko"
    var target = "en"
    var data : String? = null
    var result : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        btn.setOnClickListener {
//            var text = transText.text.toString()
//            var source = "ko"
//            var target = "en"
//            Log.d(TAG, "onCreate: $text")
//            var data = RetrofitManage.instance.getTrans(source,target, text)
//            resultText.text = data
//        }

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
                        result = RetrofitManage.instance.getTrans(source, target, data!!)
                    }
                    .launchIn(this)
                    delay(2000L)
                    withContext(Dispatchers.Main){
                        Log.d(TAG, "onCreate: 2")
                        resultText.text = result
                    }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        myCoroutineScope.cancel()
    }

}
