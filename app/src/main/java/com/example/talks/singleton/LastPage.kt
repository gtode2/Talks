package com.example.talks.singleton

object LastPage {
    private var page:String="home"
    fun setPage(p:String){
        page=p
    }
    fun getPage():String{
        return page
    }
}