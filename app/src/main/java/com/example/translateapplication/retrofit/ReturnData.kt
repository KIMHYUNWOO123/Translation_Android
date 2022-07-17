package com.example.translateapplication.retrofit

data class ReturnData(
    var message : Result
)
data class Result(
    var result : TranText
)
data class TranText(
    var translatedText : String
)