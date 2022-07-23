package com.example.translateapplication


import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.speech.tts.TextToSpeech

import android.util.Log
import android.widget.Toast
import com.example.translateapplication.coroutine.onTextChange
import com.example.translateapplication.retrofit.RetrofitManage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    var TAG = "Main"
    private var myJob: Job = Job()
    private var myCoroutineScope = CoroutineScope(Dispatchers.IO + myJob)
    private var myHandler: MyHandler? = null
    var source: String? = null
    var target: String? = null
    var data: String? = null
    private var tts : TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myHandler = MyHandler()
        transText.isEnabled = false
        initTextToSpeech()
        btn_speak.setOnClickListener {
            if(data != null){
                Log.d(TAG, "onCreate: 음성출력")
                ttsSpeak(data!!)
            }
        }
        thread(true) {
            while (true){
                if (source != null && target != null && source != target) {
                    runOnUiThread {
                        transText.isEnabled = true
                    }
                }
                if(source == target && source != null && transText.isEnabled){
                    runOnUiThread {
                        transText.isEnabled = false
                    }
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
                    it?.length!! > 0
                }
                .onEach {
                    Log.d(TAG, "onCreate: 호출")
                    data = it.toString()
                    RetrofitManage.instance.getTrans(source!!, target!!, data!!, myHandler!!)
                }
                .launchIn(this)
        }
    }
    private fun initTextToSpeech(){
        Log.d(TAG, "initTextToSpeech: 함수실행")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            return
        }
        tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
            if(it == TextToSpeech.SUCCESS){
                var result = tts?.setLanguage(Locale.ENGLISH)
                if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                    Log.d(TAG, "initTextToSpeech: 초기화 성공")
                    return@OnInitListener
                }
            }
        })
    }

    private fun ttsSpeak(text : String){
        tts?.speak(text,TextToSpeech.QUEUE_ADD, null, null)
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
