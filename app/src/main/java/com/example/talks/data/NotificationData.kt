package com.example.talks.data

import com.google.firebase.Timestamp

data class NotificationData (
    val err:Boolean = false,
    val author:String="",
    val timestamp: Timestamp=Timestamp.now(),
    val src:String="",
    val type:Int=-1
)