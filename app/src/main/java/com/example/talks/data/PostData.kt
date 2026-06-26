package com.example.talks.data

import com.google.firebase.Timestamp

data class PostData(
    var id:String="",
    var likes:Int=0,
    var title:String="",
    var post:String="",
    var source:String="",
    val uid:String="",
    var image:Boolean=false,
    var imgTimestamp: Timestamp?=null,
    var isLiked:Boolean=false,
    var isSaved:Boolean=false
)

data class PrPostData (
    var id:String?,
    var liked:Boolean,
    var saved:Boolean,
)