package com.example.talks.interfaces

interface PostHandlerInterface {
    fun incrLike(postId:String="")
    fun decrLike(postId:String="")
    fun savePost(postId:String="")
    fun unsavePost(postId:String="")
}