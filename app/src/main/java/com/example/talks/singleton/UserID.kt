package com.example.talks.singleton

object UserID {
    private var uid:String? = null
    private var temp:String?=null
    fun setUID(p:String?){
        uid=p
    }
    fun getUID():String?{
        return uid
    }
    fun setTemp(p:String){
        temp=p
    }
    fun getTemp():String?{
        return temp
    }
}