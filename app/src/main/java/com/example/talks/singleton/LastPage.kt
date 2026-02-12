package com.example.talks.singleton

object LastPage {
    private var page:String="home"
    private var homepage:String="all"
    fun setPage(p:String){
        page=p
    }
    fun getPage():String{
        return page
    }
    fun getHomepage():String{
        return homepage
    }
    fun setHomepage(p:String){
        homepage=p
    }
}