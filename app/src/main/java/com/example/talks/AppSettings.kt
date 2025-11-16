package com.example.talks

import android.app.Application

/*
data class Settings(
    val uid: String
)
*/
class AppSettings: Application(){
    var uid:String? = null
    fun setUID(id:String?){
        //imposta userId
        uid = id

        //deve anche poterlo memorizzare localmente per accessi futuri
    }
    fun getUID():String?{
        return uid
    }
}
