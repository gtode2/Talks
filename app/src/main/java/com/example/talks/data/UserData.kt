package com.example.talks.data

data class UserData(
    var Uid: String,
    var followers:Int,
    var followed:Int,
    var name:String="",
    var surname:String=""
)
