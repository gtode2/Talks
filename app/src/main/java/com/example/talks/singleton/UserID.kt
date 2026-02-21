package com.example.talks.singleton

object UserID {
    private var uid:String? = null
    fun setUID(p:String?){
        uid=p
    }
    fun getUID():String?{
        return uid
    }
}