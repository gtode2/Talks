package com.example.talks.data

data class PostData(
    var id:String="",
    var likes:Int=0,
    var title:String="",
    var post:String="",
    var source:String="",
    val uid:String="",
    val image:Boolean=false,
    var isLiked:Boolean=false,
    var isSaved:Boolean=false
)
