package com.example.talks.data

data class UserData(
    var Uid: String,
    var followers:Int=0,
    var followed:Int=0,
    var name:String="",
    var surname:String="",
    var err:String?=null
)
