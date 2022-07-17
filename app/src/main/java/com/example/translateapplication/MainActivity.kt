package com.example.translateapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.translateapplication.retrofit.RetrofitManage
import kotlinx.android.synthetic.main.activity_main.*
import okio.Utf8
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var TAG = "Main"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn.setOnClickListener {
            var text = transText.text.toString()
            var source = "ko"
            var target = "en"
            Log.d(TAG, "onCreate: $text")
            RetrofitManage.instance.getTrans(source,target, text)
        }
    }
}