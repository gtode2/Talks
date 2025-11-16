package com.example.talks.data

data class CommentData(
    val id:String="",
    val date:com.google.firebase.Timestamp?=null,
    val postid:String="",
    val text:String="",
    val uid:String=""
)