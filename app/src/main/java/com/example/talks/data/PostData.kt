package com.example.talks.data

data class PostData(
    var id:String="",
    var likes:Int=0,
    val title:String="",
    val post:String="",
    val source:String="",
    val uid:String="",
    val image:String="",
    var isLiked:Boolean=false,
    var isSaved:Boolean=false
)
